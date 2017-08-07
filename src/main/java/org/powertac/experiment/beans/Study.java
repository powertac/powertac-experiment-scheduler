package org.powertac.experiment.beans;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.constants.Constants;
import org.powertac.experiment.models.ParamMap;
import org.powertac.experiment.models.ParamMap.MapOwner;
import org.powertac.experiment.models.Parameter;
import org.powertac.experiment.models.Type;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.Utils;
import org.powertac.experiment.states.ExperimentState;
import org.powertac.experiment.states.StudyState;

import javax.faces.bean.ManagedBean;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;


@ManagedBean
@Entity
@Table(name = "studies")
public class Study implements MapOwner
{
  private static Logger log = Utils.getLogger();

  private int studyId;
  private User user;
  private String name;
  private StudyState state = StudyState.pending;

  private Map<Type, Parameter> parameterMap = new HashMap<>();
  private ParamMap paramMap = new ParamMap(this, parameterMap);
  private String variableName;
  private String variableValue;

  public Study ()
  {
  }

  @Transient
  public boolean isEditingAllowed ()
  {
    return state == StudyState.pending;
  }

  @Transient
  public boolean isSchedulingAllowed ()
  {
    return isEditingAllowed();
  }

  @Transient
  public boolean isDeletingAllowed ()
  {
    return isEditingAllowed();
  }

  public void scheduleStudy (Session session)
  {
    state = StudyState.in_progress;

    String startDate =
        Utils.dateToStringFull(Utils.offsetDate()).replace(" ", "_");
    paramMap.setOrUpdateValue(Type.createTime, startDate);
    int experimentCounter = 1;
    for (String value : ParamMap.getValueList(variableName, variableValue)) {
      createExperiment(session, experimentCounter++, variableName, value);
    }
  }

  private void createExperiment (Session session, int experimentCounter,
                                 String variableName, String variableValue)
  {
    Experiment experiment = new Experiment();
    experiment.setStudy(this);
    experiment.copyParameters(paramMap, variableName, variableValue);
    session.saveOrUpdate(experiment);
    experiment.createGames(session, experimentCounter);

    log.info(String.format("Created experiment: %s", experiment.getExperimentId()));
  }

  public void experimentCompleted (Session session, int finishedExperimentId)
  {
    boolean allDone = true;

    List<Experiment> experiments = Experiment.getAllExperiments(session);

    for (Experiment experiment : experiments) {
      // The state of the finished game isn't in the db yet.
      if (experiment.getStudy().getStudyId() != studyId ||
          experiment.getExperimentId() == finishedExperimentId) {
        continue;
      }
      allDone &= experiment.getState().equals(ExperimentState.complete);
    }

    if (allDone) {
      state = StudyState.complete;
      session.update(this);
    }

    // Always generate new CSVs
    //CSV.createRoundCsv(this);
  }

  // TODO Shouldn't be needed, use default values
  public void ensureParameters (ParamMap setMap, Location location)
  {
    // Guarantee required params
    if (setMap.get(Type.gameLength) == null) {
      setMap.put(Type.gameLength, new Parameter(this, Type.gameLength,
          Game.computeGameLength()));
    }
    if (setMap.get(Type.location) == null) {
      setMap.put(Type.location, new Parameter(this, Type.location,
          location.getLocation()));
    }
    if (setMap.get(Type.simStartDate) == null) {
      setMap.put(Type.simStartDate, new Parameter(this, Type.simStartDate,
          Utils.dateToStringSmall(location.getDateFrom())));
    }
    if (setMap.get(Type.bootstrapId) == null) {
      setMap.put(Type.bootstrapId, new Parameter(this, Type.bootstrapId, "1"));
    }

    if (variableName.isEmpty()) {
      return;
    }

    if (Type.valueOf(variableName).exclusive) {
      setMap.remove(Type.valueOf(variableName));
    }
  }

  //<editor-fold desc="Collections">
  public static List<Study> getNotCompleteSets ()
  {
    return getAllSets().stream().filter(
        p -> p.state != StudyState.complete).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public static List<Study> getAllSets ()
  {
    List<Study> studies = new ArrayList<>();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      studies = (List<Study>) session
          .createQuery(Constants.HQL.GET_EXPERIMENT_SETS)
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();

    return studies;
  }

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "studyId")
  @MapKey(name = "type")
  private Map<Type, Parameter> getParameterMap ()
  {
    return parameterMap;
  }

  private void setParameterMap (Map<Type, Parameter> parameterMap)
  {
    this.parameterMap = parameterMap;
    paramMap= new ParamMap(this, parameterMap);
  }

  @Transient
  public ParamMap getParamMap ()
  {
    return paramMap;
  }
  //</editor-fold>

  //<editor-fold desc="Setters and Getters">
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "studyId", unique = true, nullable = false)
  public int getStudyId ()
  {
    return studyId;
  }

  public void setStudyId (int studyId)
  {
    this.studyId = studyId;
  }

  @ManyToOne
  @JoinColumn(name = "userId")
  public User getUser ()
  {
    return user;
  }

  public void setUser (User user)
  {
    this.user = user;
  }

  @Column(name = "name", nullable = false)
  public String getName ()
  {
    return name;
  }

  public void setName (String name)
  {
    this.name = name;
  }

  @Column(name = "state", nullable = false)
  @Enumerated(EnumType.STRING)
  public StudyState getState ()
  {
    return state;
  }

  public void setState (StudyState state)
  {
    this.state = state;
  }

  @Column(name = "variableName", nullable = true)
  public String getVariableName ()
  {
    return variableName;
  }

  public void setVariableName (String variableName)
  {
    this.variableName = variableName;
  }

  @Column(name = "variableValue", nullable = true)
  public String getVariableValue ()
  {
    return variableValue;
  }

  public void setVariableValue (String variableValue)
  {
    this.variableValue = variableValue;
  }
//</editor-fold>
}