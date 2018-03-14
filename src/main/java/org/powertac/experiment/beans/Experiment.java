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
import org.powertac.experiment.services.Scheduler;
import org.powertac.experiment.services.Utils;
import org.powertac.experiment.states.ExperimentState;
import org.powertac.experiment.states.GameState;

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
@Table(name = "experiments")
public class Experiment implements MapOwner
{
  private static Logger log = Utils.getLogger();

  private int experimentId;
  private Study study;
  private ExperimentState state = ExperimentState.pending;

  private Map<String, Parameter> parameterMap = new HashMap<>();
  private ParamMap paramMap = new ParamMap(this, parameterMap);
  private Map<Integer, Game> gameMap = new HashMap<>();

  public Experiment ()
  {
  }

  public void copyParameters (ParamMap paramMap, String name, String value)
  {
    // Copy the params appropriate for experiments
    for (String key : paramMap.keySet()) {
      if (key.equals(Type.createTime) || key.equals(Type.startTime)) {
        continue;
      }

      if (paramMap.get(key) != null) {
        this.paramMap.createParameter(key, paramMap.get(key).getValue());
      }
    }

    // Copy the variable
    Type type = Type.get(paramMap.getPomId(), name);
    if (!name.isEmpty() && type != null) {
      this.paramMap.setOrUpdateValue(name, value, type.exclusive);
    }
  }

  public void createGames (Session session, int experimentCounter)
  {
    List<Broker> brokers = Broker.getBrokersByIds(session,
        paramMap.get(Type.brokers().name).getValue());

    int multiplier = Integer.valueOf(paramMap.get(Type.multiplier).getValue());
    for (int i = 0; i < multiplier; i++) {
      Game game = Game.createGame(this, experimentCounter, i + 1);
      session.saveOrUpdate(game);
      log.info(String.format("Created game : %s", game.getGameId()));

      for (Broker broker : brokers) {
        Agent agent = Agent.createAgent(broker, game);
        game.getAgentMap().put(broker.getBrokerId(), agent);
        session.save(agent);
        log.info(String.format("Added broker: %s", broker.getBrokerId()));
      }
    }
  }

  public void gameCompleted (Session session, int finishedGameId)
  {
    boolean allDone = true;

    for (Game game : gameMap.values()) {
      // The state of the finished game isn't in the db yet.
      if (game.getGameId() == finishedGameId) {
        continue;
      }
      allDone &= game.getState().equals(GameState.game_complete);
    }

    if (allDone) {
      state = ExperimentState.complete;
      Scheduler scheduler = Scheduler.getScheduler();
      scheduler.unloadExperiment(experimentId);

      study.experimentCompleted(session, experimentId);
    }

    // Always generate new CSVs
    //CSV.createRoundCsv(this);
  }

  @Transient
  public String getName ()
  {
    return study.getName() + "_" + experimentId;
  }

  @Transient
  public String getProgress ()
  {
    long completed = gameMap.values().stream().filter(
        p -> p.getState().equals(GameState.game_complete)).count();
    long notCompleted = gameMap.values().size() - completed;

    return String.format("%s / %s", completed, notCompleted);
  }

  //<editor-fold desc="Collections">
  public static List<Experiment> getNotCompleteExperiments ()
  {
    return getAllExperiments().stream().filter(
        p -> p.state != ExperimentState.complete).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private static List<Experiment> getAllExperiments ()
  {
    List<Experiment> experiments = new ArrayList<>();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      experiments = getAllExperiments(session);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();

    return experiments;
  }

  @SuppressWarnings("unchecked")
  public static List<Experiment> getAllExperiments (Session session)
  {
    return (List<Experiment>) session
        .createQuery(Constants.HQL.GET_EXPERIMENTS)
        .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
  }

  @OneToMany(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "experimentId")
  @MapKey(name = "gameId")
  public Map<Integer, Game> getGameMap ()
  {
    return gameMap;
  }

  public void setGameMap (Map<Integer, Game> gameMap)
  {
    this.gameMap = gameMap;
  }

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "experimentId")
  @MapKey(name = "type")
  private Map<String, Parameter> getParameterMap ()
  {
    return parameterMap;
  }

  private void setParameterMap (Map<String, Parameter> parameterMap)
  {
    this.parameterMap = parameterMap;
    paramMap = new ParamMap(this, parameterMap);
  }

  @Transient
  public ParamMap getParamMap ()
  {
    return paramMap;
  }
  //</editor-fold>

  //<editor-fold desc="Getters and setters">
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "experimentId", unique = true, nullable = false)
  public int getExperimentId ()
  {
    return experimentId;
  }

  public void setExperimentId (int experimentId)
  {
    this.experimentId = experimentId;
  }

  @ManyToOne
  @JoinColumn(name = "studyId")
  public Study getStudy ()
  {
    return study;
  }

  public void setStudy (Study study)
  {
    this.study = study;
  }

  @Column(name = "state", nullable = false)
  @Enumerated(EnumType.STRING)
  public ExperimentState getState ()
  {
    return state;
  }

  public void setState (ExperimentState state)
  {
    this.state = state;
  }
  //</editor-fold>
}
