package org.powertac.experiment.actions;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Agent;
import org.powertac.experiment.beans.Broker;
import org.powertac.experiment.beans.Experiment;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Machine;
import org.powertac.experiment.states.GameState;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.jobs.RunAbort;
import org.powertac.experiment.jobs.RunKill;
import org.powertac.experiment.services.Utils;
import org.powertac.experiment.states.AgentState;
import org.springframework.beans.factory.InitializingBean;

import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@ManagedBean
public class ActionOverview implements InitializingBean
{
  private static Logger log = Utils.getLogger();

  private List<Broker> brokerList;
  private List<Game> notCompleteGamesList;
  private List<Experiment> notCompleteExperimentList;
  private Map<Integer, Set<Integer>> runningGames;

  public ActionOverview ()
  {
  }

  public void afterPropertiesSet () throws Exception
  {
    runningGames = new HashMap<>();

    brokerList = Broker.getBrokerList();
    Set<Integer> brokerIds = new HashSet<Integer>();
    for (Broker broker : brokerList) {
      brokerIds.add(broker.getBrokerId());
      runningGames.put(broker.getBrokerId(), new HashSet<>());
    }

    notCompleteExperimentList = Experiment.getNotCompleteExperiments();
    notCompleteGamesList = new ArrayList<>();
    for (Experiment experiment : notCompleteExperimentList) {
      for (Game game : experiment.getGameMap().values()) {
        if (game.getState().equals(GameState.game_complete)) {
          continue;
        }

        notCompleteGamesList.add(game);

        for (Agent agent : game.getAgentMap().values()) {
          if (agent.getState() == AgentState.in_progress &&
              brokerIds.contains(agent.getBrokerId())) {
            runningGames.get(agent.getBrokerId()).add(game.getGameId());
          }
        }
      }
    }
  }

  public String getBrokerState (int brokerId)
  {
    return MemStore.getBrokerState(brokerId) ? "enabled" : "disabled";
  }

  public void abortGame (Game game)
  {
    log.info("Trying to abort game: " + game.getGameId());
    new RunAbort(game.getMachine().getMachineName()).run();
    Utils.growlMessage("Notice", "Aborting games takes some time, please wait");
  }

  public void killGame (Game game)
  {
    log.info("Trying to kill game: " + game.getGameId());

    int gameId = game.getGameId();
    List<Machine> machines = new ArrayList<>();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      if (game.getState() == GameState.boot_in_progress) {
        log.info("Resetting boot game: " + gameId + " on machine: " +
            game.getMachine().getMachineName());
        game.setState(GameState.boot_pending);
      }
      else if (game.getState().isRunning()) {
        log.info("Resetting sim game: " + gameId + " on machine: " +
            game.getMachine().getMachineName());
        game.setState(GameState.boot_complete);

        for (Agent agent : game.getAgentMap().values()) {
          if (agent.getState().isPending()) {
            MemStore.setBrokerState(agent.getBrokerId(), false);
          }
          machines.add(agent.getMachine());
          agent.setState(AgentState.pending);
          agent.setBalance(0);
          agent.setMachine(null);
          session.update(agent);
        }
      }

      machines.add(game.getMachine());
      game.setMachine(null);
      session.update(game);

      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();

      log.error("Failed to completely kill game: " + gameId);
      Utils.growlMessage("Failed to kill game : " + gameId);
    }
    finally {
      session.close();
    }

    // Kill the job(s) on Jenkins and the slave(s), update in 2 mins
    for (Machine machine : machines) {
      if (machine != null) {
        new RunKill(machine.getMachineName()).run();
        Machine.delayedMachineUpdate(machine, 120);
      }
    }

    // Removed MemStored info about game
    MemStore.removeGameInfo(gameId);
  }

  public void restartGame (Game game)
  {
    log.info("Trying to restart game: " + game.getGameId());

    int gameId = game.getGameId();

    Session session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      if (game.getState() == GameState.boot_failed) {
        log.info("Resetting boot game: " + gameId);
        game.setState(GameState.boot_complete);
      }
      if (game.getState() == GameState.game_failed) {
        log.info("Resetting sim game: " + gameId);
        game.setState(GameState.boot_complete);

        for (Agent agent : game.getAgentMap().values()) {
          agent.setState(AgentState.pending);
          session.update(agent);
        }
        session.flush();
      }

      game.setMachine(null);
      session.update(game);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();

      log.error("Failed to restart game: " + gameId);
      Utils.growlMessage("Failed to restart game : " + gameId);
    }
    session.close();
  }

  //<editor-fold desc="Collections">
  public List<Broker> getBrokerList ()
  {
    return brokerList;
  }

  public List<Experiment> getNotCompleteExperimentList ()
  {
    return notCompleteExperimentList;
  }

  public List<Game> getNotCompleteGamesList ()
  {
    return notCompleteGamesList;
  }

  public String getRunningGames (int brokerId)
  {
    Set<Integer> tmp = runningGames.get(brokerId);
    if (tmp == null) {
      return "";
    }
    return tmp.toString().replace("[", "").replace("]", "");
  }
  //</editor-fold>
}