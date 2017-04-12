package org.powertac.experiment.actions;

import org.apache.log4j.Logger;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.powertac.experiment.beans.Experiment;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.beans.Machine;
import org.powertac.experiment.beans.Pom;
import org.powertac.experiment.beans.User;
import org.powertac.experiment.services.CheckWeatherServer;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.services.Properties;
import org.powertac.experiment.services.Scheduler;
import org.powertac.experiment.services.SpringApplicationContext;
import org.powertac.experiment.services.Upload;
import org.powertac.experiment.services.Utils;
import org.powertac.experiment.states.MachineState;
import org.springframework.beans.factory.InitializingBean;

import javax.faces.bean.ManagedBean;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


@ManagedBean
public class ActionAdmin implements InitializingBean
{
  private static Logger log = Utils.getLogger();

  private Properties properties = Properties.getProperties();

  private List<Integer> selectedExperiments;

  private int locationId = -1;
  private String locationName = "";
  private int locationTimezone = 0;
  private Date locationStartTime = null;
  private Date locationEndTime = null;

  private UploadedFile uploadedPom;
  private String pomName;

  private int machineId = -1;
  private String machineName = "";
  private String machineUrl = "";

  private List<Experiment> availableExperiments;
  private List<Location> locationList;
  private List<Location> possibleLocations;
  private List<Pom> pomList;
  private List<Machine> machineList;
  private List<User> userList;

  public ActionAdmin ()
  {
  }

  @SuppressWarnings("unchecked")
  public void afterPropertiesSet () throws Exception
  {
    availableExperiments = Experiment.getNotCompleteExperiments();

    locationList = Location.getLocationList();

    possibleLocations = MemStore.getAvailableLocations();
    for (Location location : getLocationList()) {
      Iterator<Location> iter = possibleLocations.iterator();
      while (iter.hasNext()) {
        if (iter.next().getLocation().equals(location.getLocation())) {
          iter.remove();
        }
      }
    }
    MemStore.setAvailableLocations(possibleLocations);

    pomList = Pom.getPomList();
    machineList = Machine.getMachineList();
    userList = User.getUserList();
  }

  //<editor-fold desc="Header stuff">
  public void restartScheduler ()
  {
    log.info("Restarting Scheduler");
    Scheduler scheduler = Scheduler.getScheduler();
    if (!scheduler.restartScheduler()) {
      log.info("Not restarting Scheduler, too close");
    }
  }

  /* selectedStudies is a list of Integers that are the IDs of the rounds selected
   * by the user in the 'Admin'-form of the experiment scheduler.
   * This function gives this list to the scheduler to make sure that this will
   * be the list of running rounds.
   */
  public void loadExperiments ()
  {
    Scheduler scheduler = Scheduler.getScheduler();
    scheduler.loadExperiments(selectedExperiments);
    for (Integer experimentId : selectedExperiments) {
      log.info("Loading Experiment " + experimentId);
    }
    log.info("End of list of experiments that are loaded");
  }

  /* This function is run when the user presses the 'Unload'-button on the
   * 'Admin'-form of the experiment scheduler. It makes sure that the scheduler
   * will clear the list with running rounds.
   */
  public void unloadExperiments ()
  {
    log.info("Unloading Experiments");

    Scheduler scheduler = Scheduler.getScheduler();
    scheduler.unloadExperiments(true);
  }

  public List<Experiment> getAvailableExperiments ()
  {
    return availableExperiments;
  }

  public List<String> getConfigErrors ()
  {
    return properties.getErrorMessages();
  }

  public void removeMessage (String message)
  {
    properties.removeErrorMessage(message);
  }
  //</editor-fold>

  //<editor-fold desc="Location stuff">
  public List<Location> getLocationList ()
  {
    return locationList;
  }

  public List<Location> getPossibleLocationList ()
  {
    return possibleLocations;
  }

  public void addLocation (Location l)
  {
    locationName = l.getLocation();
    locationTimezone = l.getTimezone();
    locationStartTime = l.getDateFrom();
    locationEndTime = l.getDateTo();
  }

  public void editLocation (Location l)
  {
    locationId = l.getLocationId();
    locationName = l.getLocation();
    locationTimezone = l.getTimezone();
    locationStartTime = l.getDateFrom();
    locationEndTime = l.getDateTo();
  }

  public void saveLocation ()
  {
    if (locationName.isEmpty() || locationStartTime == null || locationEndTime == null) {
      Utils.growlMessage("Location not saved.<br/>Some fields were empty!");
      return;
    }

    if (locationId == -1) {
      addLocation();
    }
    else {
      editLocation();
    }
  }

  public void addLocation ()
  {
    Location location = new Location();
    location.setLocation(locationName);
    location.setDateFrom(locationStartTime);
    location.setDateTo(locationEndTime);
    location.setTimezone(locationTimezone);

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      session.save(location);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    if (transaction.wasCommitted()) {
      log.info("Added new location " + locationName);
      resetLocationData();
    }

    session.close();
  }

  public void editLocation ()
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Location location = (Location) session.get(Location.class, locationId);
      location.setLocation(locationName);
      location.setDateFrom(locationStartTime);
      location.setDateTo(locationEndTime);
      location.setTimezone(locationTimezone);

      session.update(location);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      Utils.growlMessage("Location not edited.<br/>" + e.getMessage());
    }
    if (transaction.wasCommitted()) {
      log.info("Edited location " + locationName);
      resetLocationData();
    }

    session.close();
  }

  public void deleteLocation (Location location)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      session.delete(location);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();
    resetLocationData();

    CheckWeatherServer checkWeatherServer = (CheckWeatherServer)
        SpringApplicationContext.getBean("checkWeatherServer");
    checkWeatherServer.loadExtraLocations();
  }

  private void resetLocationData ()
  {
    locationId = -1;
    locationName = "";
    locationTimezone = 0;
    locationStartTime = null;
    locationEndTime = null;
  }
  //</editor-fold>

  //<editor-fold desc="Pom stuff">
  public List<Pom> getPomList ()
  {
    return pomList;
  }

  public void submitPom ()
  {
    if (pomName.isEmpty()) {
      Utils.growlMessage("You need to fill in the pom name.");
      return;
    }

    if (uploadedPom == null) {
      Utils.growlMessage("You need to choose a pom file.");
      return;
    }

    Pom pom = new Pom();
    pom.setPomName(getPomName());

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      session.save(pom);
    }
    catch (ConstraintViolationException e) {
      transaction.rollback();
      session.close();
      Utils.growlMessage("This name is already used.");
      return;
    }

    Upload upload = new Upload(uploadedPom);
    String msg = upload.submit("pomLocation", pom.pomFileName());
    Utils.growlMessage(msg);

    if (msg.toLowerCase().contains("success")) {
      transaction.commit();
    }
    else {
      transaction.rollback();
    }
    session.close();
  }
  //</editor-fold>

  //<editor-fold desc="Machine stuff">
  public List<Machine> getMachineList ()
  {
    return machineList;
  }

  public void toggleAvailable (Machine machine)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      machine.setAvailable(!machine.isAvailable());
      session.update(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();
  }

  public void toggleState (Machine machine)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      if (machine.getState() == MachineState.running) {
        machine.setState(MachineState.idle);
      }
      else {
        machine.setState(MachineState.running);
      }
      session.update(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();
  }

  public void editMachine (Machine m)
  {
    machineId = m.getMachineId();
    machineName = m.getMachineName();
    machineUrl = m.getMachineUrl();
  }

  public void saveMachine ()
  {
    machineUrl = machineUrl.replace("https://", "").replace("http://", "");

    if (machineName.isEmpty() || machineUrl.isEmpty()) {
      Utils.growlMessage("Machine not saved.<br/>Some fields were empty!");
      return;
    }

    // Make sure we get a new list of IPs
    MemStore.resetMachineIPs();

    // It's a new machine
    if (machineId == -1) {
      addMachine();
    }
    else {
      editMachine();
    }
  }

  public void addMachine ()
  {
    Machine machine = new Machine();
    machine.setMachineName(machineName);
    machine.setMachineUrl(machineUrl);
    machine.setState(MachineState.idle);
    machine.setAvailable(false);

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      session.save(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      Utils.growlMessage("Machine not added.<br/>" + e.getMessage());
    }
    if (transaction.wasCommitted()) {
      log.info("Added new machine " + machineName);
      resetMachineData();
    }

    session.close();
  }

  public void editMachine ()
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Machine machine = (Machine) session.get(Machine.class, machineId);
      machine.setMachineName(machineName);
      machine.setMachineUrl(machineUrl);

      session.update(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      Utils.growlMessage("Machine not edited.<br/>" + e.getMessage());
    }
    if (transaction.wasCommitted()) {
      log.info("Edited machine " + machineName);
      resetMachineData();
    }

    session.close();
  }

  public void deleteMachine (Machine machine)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      log.info("Deleting machine " + machine.getMachineId());
      session.delete(machine);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      Utils.growlMessage("Machine not deleted.<br/>" + e.getMessage());
    }
    session.close();
    resetMachineData();
  }

  private void resetMachineData ()
  {
    machineId = -1;
    machineName = "";
    machineUrl = "";
  }
  //</editor-fold>

  //<editor-fold desc="User stuff">
  public List<User> getUserList ()
  {
    return userList;
  }

  public void increasePermissions (User user)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      user.increasePermission();
      session.update(user);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      log.warn("Error increasing permissions for : " + user.getUserId());
      e.printStackTrace();
    }
    if (transaction.wasCommitted()) {
      log.info("Increased permissions for : " + user.getUserName());
    }

    session.close();
  }

  public void decreasePermissions (User user)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      user.decreasePermission();
      session.update(user);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      log.warn("Error decreasing permissions for : " + user.getUserId());
      e.printStackTrace();
    }
    if (transaction.wasCommitted()) {
      log.info("decreased permissions for : " + user.getUserName());
    }

    session.close();
  }
  //</editor-fold>

  //<editor-fold desc="Setters and Getters">
  public int getLocationId ()
  {
    return locationId;
  }

  public void setLocationId (int locationId)
  {
    this.locationId = locationId;
  }

  public String getLocationName ()
  {
    return locationName;
  }

  public void setLocationName (String locationName)
  {
    this.locationName = locationName;
  }

  public int getLocationTimezone ()
  {
    return locationTimezone;
  }

  public void setLocationTimezone (int locationTimezone)
  {
    this.locationTimezone = locationTimezone;
  }

  public Date getLocationStartTime ()
  {
    return locationStartTime;
  }

  public void setLocationStartTime (Date locationStartTime)
  {
    this.locationStartTime = locationStartTime;
  }

  public Date getLocationEndTime ()
  {
    return locationEndTime;
  }

  public void setLocationEndTime (Date locationEndTime)
  {
    this.locationEndTime = locationEndTime;
  }

  public String getPomName ()
  {
    return pomName;
  }

  public void setPomName (String pomName)
  {
    this.pomName = pomName.trim();
  }

  public UploadedFile getUploadedPom ()
  {
    return uploadedPom;
  }

  public void setUploadedPom (UploadedFile uploadedPom)
  {
    this.uploadedPom = uploadedPom;
  }

  public int getMachineId ()
  {
    return machineId;
  }

  public void setMachineId (int machineId)
  {
    this.machineId = machineId;
  }

  public String getMachineName ()
  {
    return machineName;
  }

  public void setMachineName (String machineName)
  {
    this.machineName = machineName;
  }

  public String getMachineUrl ()
  {
    return machineUrl;
  }

  public void setMachineUrl (String machineUrl)
  {
    this.machineUrl = machineUrl;
  }

  public List<Integer> getSelectedExperiments ()
  {
    return selectedExperiments;
  }

  public void setSelectedExperiments (List<Integer> selectedExperiments)
  {
    this.selectedExperiments = selectedExperiments;
  }
  //</editor-fold>
}
