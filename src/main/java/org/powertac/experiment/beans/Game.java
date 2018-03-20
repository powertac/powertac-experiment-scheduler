package org.powertac.experiment.beans;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.constants.Constants;
import org.powertac.experiment.models.ParamMap;
import org.powertac.experiment.models.ParamMap.MapOwner;
import org.powertac.experiment.models.Parameter;
import org.powertac.experiment.models.Type;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.Properties;
import org.powertac.experiment.services.Utils;
import org.powertac.experiment.states.GameState;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Table(name = "games")
public class Game implements Serializable, MapOwner
{
  private static Logger log = Utils.getLogger();

  private static Properties properties = Properties.getProperties();

  private Integer gameId = 0;
  private String gameName;
  private Experiment experiment;
  private GameState state = GameState.boot_pending;
  private Machine machine = null;
  private String serverQueue = "";

  private Map<String, Parameter> parameterMap = new HashMap<>();
  private ParamMap paramMap = new ParamMap(this, parameterMap);
  private Map<Integer, Agent> agentMap = new HashMap<>();

  public Game ()
  {
  }

  @Transient
  @SuppressWarnings("unchecked")
  public String getBrokerIdsInGameString ()
  {
    List<Agent> agents = new ArrayList(agentMap.values());
    Collections.sort(agents, new Utils.agentIdComparator());

    StringBuilder result = new StringBuilder();
    String prefix = "";
    for (Agent agent : agents) {
      result.append(prefix).append(agent.getBroker().getBrokerId());
      prefix = ", ";
    }

    return result.toString();
  }

  @SuppressWarnings("unchecked")
  public static List<Game> getNotCompleteGamesList ()
  {
    List<Game> games = new ArrayList<>();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Query query = session.createQuery(Constants.HQL.GET_GAMES_NOT_COMPLETE);
      games = (List<Game>) query.
          setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();

    return games;
  }

  @SuppressWarnings("unchecked")
  public static List<Game> getCompleteGamesList ()
  {
    List<Game> games = new ArrayList<>();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      Query query = session.createQuery(Constants.HQL.GET_GAMES_COMPLETE);
      games = (List<Game>) query.
          setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
    }
    session.close();

    return games;
  }

  @Transient
  public String getLogURL ()
  {
    String baseUrl = properties.getProperty("logUrl");

    if (baseUrl.isEmpty()) {
      return String.format("download?game=%s", gameName);
    }

    return String.format(baseUrl, gameName);
  }

  @Transient
  public String getBrokerLogURL (int brokerId)
  {
    String baseUrl = properties.getProperty("brokerUrl");

    if (baseUrl.isEmpty()) {
      baseUrl = "download?game=%s&brokerId=%d";
    }

    return String.format(baseUrl, gameName, brokerId);
  }

  @Transient
  public List<String[]> getLogURLs ()
  {
    List<String[]> result = new ArrayList<>();

    result.add(new String[]{"Game", getLogURL()});
    for (Agent agent: getAgentMap().values()) {
      Broker broker = agent.getBroker();
      result.add(new String[]{broker.getBrokerName(),
          getBrokerLogURL(broker.getBrokerId())});
    }

    return result;
  }

  @Transient
  public String getBootLocation ()
  {
    String logLoc = properties.getProperty("bootLocation");
    return String.format("%s%s.xml", logLoc, gameName);
  }

  // Computes a random game length as outlined in the game specification
  public static int computeGameLength ()
  {
    int minLength = properties.getPropertyInt("competition.minimumTimeslotCount");
    int expLength = properties.getPropertyInt("competition.expectedTimeslotCount");

    if (expLength == minLength) {
      return minLength;
    }
    else {
      Random random = new Random();
      double roll = random.nextDouble();
      // compute k = ln(1-roll)/ln(1-p) where p = 1/(exp-min)
      double k = (Math.log(1.0 - roll) /
          Math.log(1.0 - 1.0 / (expLength - minLength + 1)));
      return minLength + (int) Math.floor(k);
    }
  }

  public static Game getGameById (int gameId)
  {
    Game game = null;
    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      game = getGame(session, gameId);
      transaction.commit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (!transaction.wasCommitted()) {
        transaction.rollback();
      }
      session.close();
    }

    return game;
  }

  public static Game getGame (Session session, int gameId)
  {
    Query query = session.createQuery(Constants.HQL.GET_GAME_BY_ID);
    query.setInteger("gameId", gameId);
    return (Game) query.uniqueResult();
  }

  public static Game createGame (Experiment experiment,
                                 String variableName, String variableValue,
                                 int experimentCounter, int gameCounter)
  {
    String gameName = String.format("%s_%d_%d",
        experiment.getStudy().getName(), experimentCounter, gameCounter);

    Game game = new Game();
    game.setGameName(gameName);
    game.setExperiment(experiment);
    game.setServerQueue(Utils.createQueueName());

    // Always generate boot for first game in experiment, or when not re-using
    boolean reuseBoot = experiment.getParamMap().getReuseBoot();
    if (gameCounter == 1 || !reuseBoot) {
      game.setState(GameState.boot_pending);
    }
    else {
      game.setState(GameState.boot_complete);
    }
    game.cloneParams(experiment, variableName, variableValue);

    log.info(String.format("Created game (%s) : %s = %s",
        game.getGameId(), variableName, variableValue));

    return game;
  }

  private void cloneParams (Experiment experiment,
                            String variableName, String variableValue)
  {
    ParamMap experimentMap = experiment.getParamMap();
    for (Type type : Type.getGameTypes()) {
      paramMap.createParameter(type.name, experimentMap.get(type.name).getValue());
    }
    paramMap.createParameter(variableName, variableValue);
  }

  //<editor-fold desc="Collections">
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "gameId")
  @MapKey(name = "type")
  private Map<String, Parameter> getParameterMap ()
  {
    return parameterMap;
  }

  private void setParameterMap (Map<String, Parameter> parameterMap)
  {
    this.parameterMap = parameterMap;
    paramMap= new ParamMap(this, parameterMap);
  }

  @Transient
  public ParamMap getParamMap ()
  {
    return paramMap;
  }

  @OneToMany(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "gameId")
  @MapKey(name = "brokerId")
  public Map<Integer, Agent> getAgentMap ()
  {
    return agentMap;
  }

  public void setAgentMap (Map<Integer, Agent> agentMap)
  {
    this.agentMap = agentMap;
  }
  //</editor-fold>

  //<editor-fold desc="Setter and getters">
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "gameId", unique = true, nullable = false)
  public Integer getGameId ()
  {
    return gameId;
  }

  public void setGameId (Integer gameId)
  {
    this.gameId = gameId;
  }

  @Column(name = "gameName")
  public String getGameName ()
  {
    return gameName;
  }

  public void setGameName (String gameName)
  {
    this.gameName = gameName;
  }

  @ManyToOne
  @JoinColumn(name = "experimentId")
  public Experiment getExperiment ()
  {
    return experiment;
  }

  public void setExperiment (Experiment experiment)
  {
    this.experiment = experiment;
  }

  @ManyToOne
  @JoinColumn(name = "machineId")
  public Machine getMachine ()
  {
    return machine;
  }

  public void setMachine (Machine machine)
  {
    this.machine = machine;
  }

  @Column(name = "state", nullable = false)
  @Enumerated(EnumType.STRING)
  public GameState getState ()
  {
    return state;
  }

  public void setState (GameState state)
  {
    this.state = state;
  }

  @Column(name = "serverQueue")
  public String getServerQueue ()
  {
    return serverQueue;
  }

  public void setServerQueue (String name)
  {
    this.serverQueue = name;
  }
  //</editor-fold>
}
