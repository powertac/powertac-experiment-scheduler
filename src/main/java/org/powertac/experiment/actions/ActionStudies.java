package org.powertac.experiment.actions;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.powertac.experiment.beans.Experiment;
import org.powertac.experiment.beans.Location;
import org.powertac.experiment.beans.Pom;
import org.powertac.experiment.beans.Study;
import org.powertac.experiment.beans.User;
import org.powertac.experiment.models.ParamEntry;
import org.powertac.experiment.models.ParamMap;
import org.powertac.experiment.models.Parameter;
import org.powertac.experiment.models.Type;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.services.Utils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@ManagedBean
@SessionScoped
public class ActionStudies implements Serializable
{
  private static Logger log = Utils.getLogger();

  private enum RadioOptions
  {
    values, csv
  }

  // Amount of parameters to edit
  private static int minimumLength = 15;

  private int studyId;
  private String studyName;
  private String variableName;
  private String variableValue;
  private String variableMin;
  private String variableMax;
  private String variableStep;
  private String valuesType;
  private int selectedPomId;

  private List<Location> availableLocations;
  private List<Study> studyList;
  private List<String[]> paramsGlobal;
  private List<Pom> pomList;

  private List<ParamEntry> paramList;

  public ActionStudies ()
  {
  }

  @PostConstruct
  public void afterPropertiesSet ()
  {
    resetValues();

    if (studyList == null) {
      studyList = Study.getAllStudies();
    }

    if (availableLocations == null) {
      availableLocations = Location.getLocationList();
    }

    if (paramsGlobal == null) {
      paramsGlobal = Parameter.getAvailableBaseParams();
    }
  }

  private void resetValues ()
  {
    studyId = -1;
    studyName = "";
    variableName = "";
    variableValue = "";
    variableMin = "";
    variableMax = "";
    variableStep = "";
    valuesType = RadioOptions.values.toString();
    pomList = Pom.getPomList();
    selectedPomId = pomList.get(pomList.size() - 1).getPomId();
    paramList = Parameter.getDefaultList(minimumLength);
  }

  public void createOrUpdateStudy ()
  {
    ParamMap paramMap = createParamMap(paramList);

    if (!inputsValidated(paramMap)) {
      if (studyId != -1) {
        resetValues();
      }
      return;
    }

    String type = studyId != -1 ? "Update" : "Create";
    saveStudy(paramMap, type, studyId);
  }

  private void ensureVariableValue (int pomId)
  {
    if (valuesType.equals(RadioOptions.csv.toString())) {
      variableValue = ParamMap.parseMinMaxStep(
          variableName, variableMin, variableMax, variableStep, pomId);
    }
    else {
      variableValue = variableValue.replace(" ", "");
    }
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
          updateStudy(study, paramMap);
          break;
        case "Update":
          study = (Study) session.get(Study.class, studyId);
          updateStudy(study, paramMap);
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
    valuesType = RadioOptions.values.toString();
    selectedPomId = study.getParamMap().getPomId();
    paramList = Parameter.getParamList(study.getParamMap(), minimumLength);

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

  private void updateStudy (Study study, ParamMap paramMap)
  {
    study.setUser(User.getCurrentUser());
    study.setName(studyName);
    study.setVariableName(variableName);
    study.setVariableValue(variableValue);

    ParamMap studyParamMap = study.getParamMap();
    // First remove params that aren't there anymore
    for (Iterator<String> it = studyParamMap.keySet().iterator(); it.hasNext(); ) {
      if (paramMap.get(it.next()) == null) {
        it.remove();
      }
    }
    // Add or update params
    for (Parameter parameter : paramMap.values()) {
      studyParamMap.setOrUpdateValue(parameter.getType(), parameter.getValue());
    }
    // Add required params, check conflicts
    study.ensureParameters(studyParamMap, availableLocations.get(0));
  }

  private ParamMap createParamMap (List<ParamEntry> paramList)
  {
    ParamMap paramMap = new ParamMap();

    for (ParamEntry entry : paramList) {
      if (entry.isEmpty()) {
        continue;
      }

      String name = entry.getName().trim();
      String value = entry.getValue().trim();

      if (value.isEmpty()) {
        log.warn("Ignoring parameter " + name + ", value is empty");
        Utils.growlMessage("Ignoring parameter " + name + ", value is empty");
      }
      else if (name.isEmpty()) {
        log.warn("Ignoring parameter, name is empty : value is " + value);
        Utils.growlMessage("Ignoring parameter, name is empty : value is " + value);
      }

      paramMap.put(name, new Parameter(null, name, value));
    }

    Pom pom = pomList.get(selectedPomId - 1);
    paramMap.put(Type.pomId,
        new Parameter(null, Type.pomId, String.valueOf(pom.getPomId())));

    return paramMap;
  }

  private boolean inputsValidated (ParamMap paramMap)
  {
    List<String> messages = new ArrayList<>();
    int pomId = paramMap.getPomId();

    // Ensure the variableValue
    ensureVariableValue(pomId);

    // Validate the variableName and non-emptyness of variableValue
    ParamMap.validateVariableName(messages, variableName, variableValue, pomId);

    // Validate the variableValue
    ParamMap.validateVariable(messages, variableName, variableValue, pomId);

    List<String> studyMapMessages = Parameter.validateStudyMap(paramMap);
    messages.addAll(studyMapMessages);

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
    return Parameter.getAvailableServerParams(selectedPomId);
  }

  public List<Pom> getPomList ()
  {
    return pomList;
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

  public int getSelectedPomId ()
  {
    return selectedPomId;
  }

  public void setSelectedPomId (int selectedPomId)
  {
    this.selectedPomId = selectedPomId;
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

  public String getVariableMin ()
  {
    return variableMin;
  }

  public void setVariableMin (String variableMin)
  {
    this.variableMin = variableMin.trim();
  }

  public String getVariableMax ()
  {
    return variableMax;
  }

  public void setVariableMax (String variableMax)
  {
    this.variableMax = variableMax.trim();
  }

  public String getVariableStep ()
  {
    return variableStep;
  }

  public void setVariableStep (String variableStep)
  {
    this.variableStep = variableStep.trim();
  }

  public String getValuesType ()
  {
    return valuesType;
  }

  public void setValuesType (String valuesType)
  {
    this.valuesType = valuesType;
  }

  public RadioOptions[] getRadioOptions ()
  {
    return RadioOptions.values();
  }

  public List<ParamEntry> getParamList ()
  {
    return paramList;
  }

  public void setParamList (List<ParamEntry> paramList)
  {
    this.paramList = paramList;
  }
  //</editor-fold>
}
