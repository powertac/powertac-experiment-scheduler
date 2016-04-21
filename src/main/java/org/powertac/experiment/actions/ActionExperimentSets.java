package org.powertac.experiment.actions;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.powertac.experiment.beans.ExperimentSet;
import org.powertac.experiment.beans.Location;
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
public class ActionExperimentSets
{
  private static Logger log = Utils.getLogger();

  private int experimentSetId;
  private String experimentSetName;
  private String variableName;
  private String variableValue;
  private String paramString;

  private List<Location> availableLocations;
  private List<ExperimentSet> experimentSetList;
  private List<String[]> paramsGlobal;
  private List<String[]> paramsServer;

  public ActionExperimentSets ()
  {
  }

  @PostConstruct
  public void afterPropertiesSet ()
  {
    if (experimentSetList == null) {
      experimentSetList = ExperimentSet.getNotCompleteSets();
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
    experimentSetId = -1;
    experimentSetName = "";
    variableName = "";
    variableValue = "";
    paramString = Parameter.getDefaultString();
  }

  public void createOrUpdateSet ()
  {
    ParamMap paramMap = createParamMap(paramString);

    if (!inputsValidated(paramMap)) {
      if (experimentSetId != -1) {
        resetValues();
      }
      return;
    }

    String type = experimentSetId != -1 ? "Update" : "Create";
    saveExperimentSet(paramMap, type, experimentSetId);
  }

  private void saveExperimentSet (ParamMap paramMap, String type, int setId)
  {
    String name = type.substring(0, type.length() - 1);
    log.info(String.format("%sing ExperimentSet", name));

    experimentSetName = experimentSetName.trim();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    ExperimentSet experimentSet = null;

    try {
      switch (type) {
        case "Create":
          experimentSet = new ExperimentSet();
          updateSet(experimentSet, paramMap);
          break;
        case "Update":
          experimentSet = (ExperimentSet) session.get(ExperimentSet.class, setId);
          updateSet(experimentSet, paramMap);
          break;
        case "Schedule":
          experimentSet = (ExperimentSet) session.get(ExperimentSet.class, setId);
          experimentSet.scheduleExperimentSet(session);
          MemStore.getNameMapping(true);
          break;
      }

      session.saveOrUpdate(experimentSet);
      transaction.commit();
    }
    catch (ConstraintViolationException ignored) {
      ignored.printStackTrace();
      transaction.rollback();
      Utils.growlMessage("The ExperimentSet name already exists");
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      String msg = String.format("Error %sing ExperimentSet", name);
      log.error(msg);
      Utils.growlMessage(msg);
    }
    finally {
      if (transaction.wasCommitted()) {
        if (experimentSet != null) {
          log.info(String.format("%sd ExperimentSet %s", type,
              experimentSet.getExperimentSetId()));
        }
        resetValues();
      }
      session.close();
    }

    experimentSetList = ExperimentSet.getNotCompleteSets();
  }

  public void editExperimentSet (ExperimentSet experimentSet)
  {
    experimentSetId = experimentSet.getExperimentSetId();
    experimentSetName = experimentSet.getName();
    variableName = experimentSet.getVariableName();
    variableValue = experimentSet.getVariableValue();
    paramString = Parameter.getParamsString(experimentSet.getParamMap());

    log.info("Editing experiment_set : " + experimentSetId
        + " " + experimentSet.getName());
  }

  public void scheduleExperimentSet (ExperimentSet experimentSet)
  {
    saveExperimentSet(null, "Schedule", experimentSet.getExperimentSetId());
  }

  public void deleteExperimentSet (ExperimentSet experimentSet)
  {
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      for (Parameter parameter : experimentSet.getParamMap().values()) {
        session.delete(parameter);
      }
      session.delete(experimentSet);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      String msg = String.format("Error deleting ExperimentSet %s", experimentSet.getName());
      log.error(msg);
      Utils.growlMessage(msg);
    }
    finally {
      if (transaction.wasCommitted()) {
        resetValues();
      }
      session.close();
    }
  }

  private void updateSet (ExperimentSet experimentSet, ParamMap paramMap)
  {
    experimentSet.setUser(User.getCurrentUser());
    experimentSet.setName(experimentSetName);
    experimentSet.setVariableName(variableName);
    experimentSet.setVariableValue(variableValue);

    ParamMap setMap = experimentSet.getParamMap();

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
    Parameter.ensureParameters(experimentSet, setMap,
        variableName, availableLocations.get(0));
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
    messages.addAll(Parameter.validateExperimentSetMap(paramMap));

    if (experimentSetName.trim().isEmpty()) {
      messages.add("The ExperimentSet name cannot be empty");
    }

    for (String msg : messages) {
      Utils.growlMessage(msg);
    }

    return messages.size() == 0;
  }

  //<editor-fold desc="Collections">
  public List<ExperimentSet> getExperimentSetList ()
  {
    return experimentSetList;
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
  public int getExperimentSetId ()
  {
    return experimentSetId;
  }

  public void setExperimentSetId (int experimentSetId)
  {
    this.experimentSetId = experimentSetId;
  }

  public String getExperimentSetName ()
  {
    return experimentSetName;
  }

  public void setExperimentSetName (String experimentSetName)
  {
    this.experimentSetName = experimentSetName;
  }

  public String getVariableValue ()
  {
    return variableValue;
  }

  public void setVariableValue (String variableValue)
  {
    this.variableValue = variableValue;
  }

  public String getVariableName ()
  {
    return variableName;
  }

  public void setVariableName (String variableName)
  {
    this.variableName = variableName;
  }

  public String getParamString ()
  {
    return paramString;
  }

  public void setParamString (String paramString)
  {
    this.paramString = paramString;
  }
  //</editor-fold>
}