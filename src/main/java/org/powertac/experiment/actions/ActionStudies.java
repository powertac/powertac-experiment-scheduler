package org.powertac.experiment.actions;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.powertac.experiment.beans.Experiment;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.beans.Study;
import org.powertac.experiment.beans.User;
import org.powertac.experiment.models.ParamMap;
import org.powertac.experiment.models.Parameter;
import org.powertac.experiment.models.Type;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.services.Utils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


@ManagedBean
public class ActionStudies
{
  private static Logger log = Utils.getLogger();

  private int studyId;
  private String studyName;
  private String variableName;
  private String variableValue;
  private String paramString;

  private List<Location> availableLocations;
  private List<Study> studyList;
  private List<String[]> paramsGlobal;
  private List<String[]> paramsServer;

  public ActionStudies ()
  {
  }

  @PostConstruct
  public void afterPropertiesSet ()
  {
    if (studyList == null) {
      studyList = Study.getAllStudies();
    }

    if (availableLocations == null) {
      availableLocations = Location.getLocationList();
    }

    if (paramsGlobal == null || paramsServer == null) {
      paramsGlobal = new ArrayList<>();
      paramsServer = new ArrayList<>();
      List<String[]> availableParams = Parameter.getAvailableParams();
      for (String[] param : availableParams) {
        if (param[0].contains("_")) {
          paramsServer.add(param);
        }
        else {
          paramsGlobal.add(param);
        }
      }
    }

    resetValues();
  }

  private void resetValues ()
  {
    studyId = -1;
    studyName = "";
    variableName = "";
    variableValue = "";
    paramString = Parameter.getDefaultString();
  }

  public void createOrUpdateSet ()
  {
    ParamMap paramMap = createParamMap(paramString);

    if (!inputsValidated(paramMap)) {
      if (studyId != -1) {
        resetValues();
      }
      return;
    }

    String type = studyId != -1 ? "Update" : "Create";
    saveStudy(paramMap, type, studyId);
  }

  private void saveStudy (ParamMap paramMap, String type, int studyId)
  {
    String name = type.substring(0, type.length() - 1);
    log.info(String.format("%sing Study", name));

    studyName = studyName.trim();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    Study study = null;

    try {
      switch (type) {
        case "Create":
          study = new Study();
          updateSet(study, paramMap);
          break;
        case "Update":
          study = (Study) session.get(Study.class, studyId);
          updateSet(study, paramMap);
          break;
        case "Schedule":
          study = (Study) session.get(Study.class, studyId);
          study.scheduleStudy(session);
          MemStore.getNameMapping(true);
          break;
        case "Pause":
          study = (Study) session.get(Study.class, studyId);
          study.pauseStudy();
          MemStore.getNameMapping(true);
          break;
      }

      session.saveOrUpdate(study);
      transaction.commit();
    }
    catch (ConstraintViolationException ignored) {
      ignored.printStackTrace();
      transaction.rollback();
      Utils.growlMessage("The Study name already exists");
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      String msg = String.format("Error %sing Study", name);
      log.error(msg);
      Utils.growlMessage(msg);
    }
    finally {
      if (transaction.wasCommitted()) {
        if (study != null) {
          log.info(String.format("%sd Study %s", type, study.getStudyId()));
        }
        resetValues();
      }
      session.close();
    }

    studyList = Study.getAllStudies();
  }

  public void editStudy (Study study)
  {
    studyId = study.getStudyId();
    studyName = study.getName();
    variableName = study.getVariableName();
    variableValue = study.getVariableValue();
    paramString = Parameter.getParamsString(study.getParamMap());

    log.info("Editing study : " + studyId + " " + study.getName());
  }

  public void scheduleStudy (Study study)
  {
    saveStudy(null, "Schedule", study.getStudyId());
    Utils.growlMessage("Study ready",
        "Email <a href=\"mailto:buijs@rsm.nl\">admin</a> for scheduling");
  }

  public void pauseStudy (Study study)
  {
    saveStudy(null, "Pause", study.getStudyId());
  }

  public void deleteStudy (Study study)
  {
    // Remove sim and broker logfiles
    study.removeLogFiles();

    // Remove study (experiments, games) from the DB
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      study = (Study) session.get(Study.class, study.getStudyId());
      for (Experiment experiment : Experiment.getAllExperiments(session)) {
        if (experiment.getStudy().getStudyId() == study.getStudyId()) {
          session.delete(experiment);
        }
      }
      session.delete(study);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      String msg = String.format("Error deleting Study %s", study.getName());
      log.error(msg);
      Utils.growlMessage(msg);
    }
    finally {
      if (transaction.wasCommitted()) {
        resetValues();
      }
      session.close();
    }

    resetValues();
    studyList = Study.getAllStudies();
  }

  private void updateSet (Study study, ParamMap paramMap)
  {
    study.setUser(User.getCurrentUser());
    study.setName(studyName);
    study.setVariableName(variableName);
    study.setVariableValue(variableValue);

    ParamMap setMap = study.getParamMap();

    // First remove params that aren't there anymore
    for (Iterator<Type> it = setMap.keySet().iterator(); it.hasNext(); ) {
      if (paramMap.get(it.next()) == null) {
        it.remove();
      }
    }
    // Add or update params
    for (Parameter parameter : paramMap.values()) {
      setMap.setOrUpdateValue(parameter.getType(), parameter.getValue());
    }
    // Add required params, check conflicts
    study.ensureParameters(setMap, availableLocations.get(0));
  }

  private ParamMap createParamMap (String paramString)
  {
    ParamMap paramMap = new ParamMap();

    String[] lines = paramString.replace(" ", "").split("\n");
    for (String line : lines) {
      try {
        String[] parts = line.split("=");
        if (parts.length != 2) {
          log.warn("Ignoring parameter, length of '=' != 2 : " +
              Arrays.toString(parts));
          Utils.growlMessage("Parameter removed because '=' isn't allowed in " +
              "line :<br/>" + line);
          continue;
        }

        String value = parts[1].trim();
        Type type = Type.valueOf(parts[0].trim());
        paramMap.put(type, new Parameter(null, type, value));
      }
      catch (Exception e) {
        System.out.println("\ncreateParamMap\n" + e.getMessage());
      }
    }

    return paramMap;
  }

  private boolean inputsValidated (ParamMap paramMap)
  {
    List<String> messages = ParamMap.validateVariable(variableName, variableValue);
    List<String> setMapMessages = Parameter.validateStudyMap(paramMap);
    messages.addAll(setMapMessages);

    if (studyName.trim().isEmpty()) {
      messages.add("The Study name cannot be empty");
    }

    for (String msg : messages) {
      Utils.growlMessage(msg);
    }

    return messages.size() == 0;
  }

  //<editor-fold desc="Collections">
  public List<Study> getStudyList ()
  {
    return studyList;
  }

  public List<String[]> getParamsGlobal ()
  {
    return paramsGlobal;
  }

  public List<String[]> getParamsServer ()
  {
    return paramsServer;
  }
  //</editor-fold>

  //<editor-fold desc="Setters and Getters">
  public int getStudyId ()
  {
    return studyId;
  }

  public void setStudyId (int studyId)
  {
    this.studyId = studyId;
  }

  public String getStudyName ()
  {
    return studyName;
  }

  public void setStudyName (String studyName)
  {
    this.studyName = studyName.trim();
  }

  public String getVariableValue ()
  {
    return variableValue;
  }

  public void setVariableValue (String variableValue)
  {
    this.variableValue = variableValue.trim();
  }

  public String getVariableName ()
  {
    return variableName;
  }

  public void setVariableName (String variableName)
  {
    this.variableName = variableName.trim();
  }

  public String getParamString ()
  {
    return paramString;
  }

  public void setParamString (String paramString)
  {
    this.paramString = paramString.trim();
  }
  //</editor-fold>
}
