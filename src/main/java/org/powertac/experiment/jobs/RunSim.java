package org.powertac.experiment.jobs;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Agent;
import org.powertac.experiment.beans.Broker;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Machine;
import org.powertac.experiment.schedulers.GamesScheduler;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.JenkinsConnector;
import org.powertac.experiment.services.Properties;
import org.powertac.experiment.services.Utils;
import org.powertac.experiment.states.ExperimentState;
import org.powertac.experiment.states.GameState;
import org.powertac.experiment.states.MachineState;

import java.util.List;


public class RunSim
{
  private static Logger log = Utils.getLogger();

  private Game game;
  private List<Machine> freeMachines;
  private String brokers = "";
  private Properties properties = Properties.getProperties();
  private Session session;

  public RunSim (Game game, List<Machine> freeMachines)
  {
    this.game = game;
    this.freeMachines = freeMachines;
  }

  private void run ()
  {
    session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      if (!checkBrokers()) {
        transaction.rollback();
        return;
      }

      if (!startAgents()) {
        transaction.rollback();
        return;
      }

      setMachineToGame();
      startJob();
      session.merge(game);
      game.getExperiment().setState(ExperimentState.in_progress);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      log.info("Failed to start sim game: " + game.getGameId());
    }
    finally {
      session.close();
    }
  }

  private boolean checkBrokers ()
  {
    // This should never happen
    if (game.getAgentMap().size() < 1) {
      log.info(String.format("Game: %s (experiment %s) reports no brokers "
          + "registered", game.getGameId(), game.getExperiment().getExperimentId()));
      return false;
    }

    for (Agent agent : game.getAgentMap().values()) {
      Broker broker = agent.getBroker();
      brokers += broker.getBrokerName() + "/" + agent.getBrokerQueue() + ",";
    }
    brokers = brokers.substring(0, brokers.length() - 1);
    return true;
  }

  private boolean startAgents ()
  {
    try {
      for (Agent agent : game.getAgentMap().values()) {
        Machine machine = freeMachines.remove(0);
        machine.setState(MachineState.running);
        session.update(machine);

        new RunAgent(agent, game, machine).run();
        session.update(agent);

        log.info(String.format("Agent: %s running on machine: %s",
            agent.getAgentId(), machine.getMachineName()));
      }

      return true;
    }
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /*
   * Link machine to the game
   */
  private void setMachineToGame ()
  {
    log.info("Claiming free machine for game " + game.getGameId());

    Machine freeMachine = freeMachines.remove(0);
    game.setMachine(freeMachine);
    freeMachine.setState(MachineState.running);
    session.update(freeMachine);
    log.info(String.format("Game: %s running on machine: %s",
        game.getGameId(), freeMachine.getMachineName()));
  }

  /*
   * If all conditions are met (we have a slave available, game is booted and
   * agents should be available) send job to Jenkins.
   */
  private void startJob () throws Exception
  {
    String finalUrl =
        properties.getProperty("jenkins.location")
            + "job/start-sim-server/buildWithParameters?"
            + "tourneyUrl=" + properties.getProperty("tourneyUrl")
            + "&pomId=" + game.getParamMap().getPomId()
            + "&gameName=" + game.getGameName()
            + "&gameId=" + game.getGameId()
            + "&machine=" + game.getMachine().getMachineName()
            + "&brokers=" + brokers
            + "&serverQueue=" + game.getServerQueue();

    log.info("Final url: " + finalUrl);

    try {
      JenkinsConnector.sendJob(finalUrl);

      log.info("Jenkins request to start sim game: " + game.getGameId());
      game.setState(GameState.game_pending);
      log.debug(String.format("Update game: %s to %s", game.getGameId(),
          GameState.game_pending));
    }
    catch (Exception e) {
      log.error("Jenkins failure to start sim game: " + game.getGameId());
      game.setState(GameState.game_failed);
      throw e;
    }
  }

  /*
   * Look for runnable games.
   * This means games that are 'boot_complete' and belong to a loaded experiment.
   */
  public static void startRunnableGames (List<Integer> runningExperimentIds,
                                         List<Game> notCompleteGames,
                                         List<Machine> freeMachines)
  {
    log.info("Looking for Runnable Games");

    if (runningExperimentIds == null || runningExperimentIds.size() == 0) {
      log.info("No experiments available for runnable games");
      return;
    }

    List<Game> games =
        GamesScheduler.getStartableGames(runningExperimentIds, notCompleteGames);

    log.info(String.format("Found %s game(s) ready to start", games.size()));

    for (Game game : games) {
      if (freeMachines.size() == 0) {
        log.info("No free machines, stop looking for Startable Games");
        return;
      }

      int brokersNeeded = game.getAgentMap().size();
      if (freeMachines.size() < (brokersNeeded + 1)) {
        log.info("Not enough machines for game: " + game.getGameId());
        continue;
      }

      log.info(String.format("Game %s will be started ...", game.getGameId()));
      new RunSim(game, freeMachines).run();
    }
  }
}
