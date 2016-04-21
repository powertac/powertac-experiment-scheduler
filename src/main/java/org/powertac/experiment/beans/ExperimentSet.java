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
import org.powertac.experiment.states.ExperimentSetState;
import org.powertac.experiment.states.ExperimentState;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static javax.persistence.GenerationType.IDENTITY;


@ManagedBean
@Entity
@Table(name = "experimentsets")
public class ExperimentSet implements MapOwner
{
  private static Logger log = Utils.getLogger();

  private int experimentSetId;
  private User user;
  private String name;
  private ExperimentSetState state = ExperimentSetState.pending;

  private Map<Type, Parameter> parameterMap = new HashMap<>();
  private ParamMap paramMap = new ParamMap(this, parameterMap);
  private String variableName;
  private String variableValue;

  public ExperimentSet ()
  {
  }

  @Transient
  public boolean isEditingAllowed ()
  {
    return state == ExperimentSetState.pending;
  }

  @Transient
  public boolean isSchedulingAllowed ()
  {
    // TODO Check other requirements

    return isEditingAllowed();
  }

  @Transient
  public boolean isDeletingAllowed ()
  {
    // TODO Check other requirements

    return isEditingAllowed();
  }

  public void scheduleExperimentSet (Session session)
  {
    state = ExperimentSetState.in_progress;

    String startDate =
        Utils.dateToStringFull(Utils.offsetDate()).replace(" ", "_");
    paramMap.setOrUpdateValue(Type.createTime, startDate);
    AtomicInteger counter = new AtomicInteger(1);
    for (String value : ParamMap.getValueList(variableName, variableValue)) {
      createExperiment(session, counter, variableName, value);
    }
  }

  private void createExperiment (Session session, AtomicInteger counter,
                                 String variableName, String variableValue)
  {
    Experiment experiment = new Experiment();
    experiment.setExperimentSet(this);
    experiment.copyParameters(paramMap, variableName, variableValue);
    session.saveOrUpdate(experiment);
    experiment.createGames(session, counter);

    log.info(String.format("Created experiment: %s", experiment.getExperimentId()));
  }

  public void experimentCompleted (Session session, int finishedExperimentId)
  {
    boolean allDone = true;

    List<Experiment> experiments = Experiment.getAllExperiments(session);

    for (Experiment experiment : experiments) {
      // The state of the finished game isn't in the db yet.
      if (experiment.getExperimentSet().getExperimentSetId() != experimentSetId ||
          experiment.getExperimentId() == finishedExperimentId) {
        continue;
      }
      allDone &= experiment.getState().equals(ExperimentState.complete);
    }

    if (allDone) {
      state = ExperimentSetState.complete;
      session.update(this);
    }

    // Always generate new CSVs
    // TODO
    //CSV.createRoundCsv(this);
  }

  //<editor-fold desc="Collections">
  public static List<ExperimentSet> getNotCompleteSets ()
  {
    return getAllSets().stream().filter(
        p -> p.state != ExperimentSetState.complete).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public static List<ExperimentSet> getAllSets ()
  {
    List<ExperimentSet> experimentSets = new ArrayList<>();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      experimentSets = (List<ExperimentSet>) session
          .createQuery(Constants.HQL.GET_EXPERIMENT_SETS)
          .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();

    return experimentSets;
  }

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "experimentSetId")
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
  @Column(name = "experimentSetId", unique = true, nullable = false)
  public int getExperimentSetId ()
  {
    return experimentSetId;
  }

  public void setExperimentSetId (int experimentSetId)
  {
    this.experimentSetId = experimentSetId;
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
  public ExperimentSetState getState ()
  {
    return state;
  }

  public void setState (ExperimentSetState state)
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