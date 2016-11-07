package org.powertac.experiment.jobs;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Machine;
import org.powertac.experiment.models.Type;
import org.powertac.experiment.schedulers.GamesScheduler;
import org.powertac.experiment.services.HibernateUtil;
import org.powertac.experiment.services.JenkinsConnector;
import org.powertac.experiment.services.Properties;
import org.powertac.experiment.services.Utils;
import org.powertac.experiment.states.GameState;
import org.powertac.experiment.states.MachineState;

import java.util.List;


public class RunBoot
{
  private static Logger log = Utils.getLogger();

  private Properties properties = Properties.getProperties();

  private Game game;
  private List<Machine> freeMachines;
  private Session session;

  public RunBoot (Game game, List<Machine> freeMachines)
  {
    this.game = game;
    this.freeMachines = freeMachines;
  }

  private void run ()
  {
    session = HibernateUtil.getSession();
    Transaction transaction = session.beginTransaction();
    try {
      setMachineToGame();
      startJob();
      session.update(game);
      transaction.commit();
    }
    catch (Exception e) {
      transaction.rollback();
      e.printStackTrace();
      log.info("Failed to bootstrap game: " + game.getGameId());
    }
    finally {
      session.close();
    }
  }

  /**
   * Link machine to the game
   */
  private void setMachineToGame ()
  {
    log.info("Claiming free machine");

    Machine freeMachine = freeMachines.remove(0);
    game.setMachine(freeMachine);
    freeMachine.setState(MachineState.running);
    session.saveOrUpdate(freeMachine);
    log.info(String.format("Game: %s booting on machine: %s",
        game.getGameId(), game.getMachine().getMachineName()));
  }

  /*
   * If all conditions are met (we have a slave available) send job to Jenkins.
   */
  private void startJob () throws Exception
  {
    String finalUrl =
        properties.getProperty("jenkins.location")
            + "job/start-boot-server/buildWithParameters?"
            + "tourneyUrl=" + properties.getProperty("tourneyUrl")
            + "&pomId=" + game.getParamMap().getPomId()
            + "&gameName=" + game.getGameName()
            + "&gameId=" + game.getGameId()
            + "&machine=" + game.getMachine().getMachineName();

    log.info("Final url: " + finalUrl);

    try {
      JenkinsConnector.sendJob(finalUrl);

      log.info("Jenkins request to bootstrap game: " + game.getGameId());
      game.setState(GameState.boot_in_progress);
      game.getParamMap().setOrUpdateValue(Type.startTime, Utils.dateToStringFull(Utils.offsetDate()));
      log.debug(String.format("Update game: %s to %s", game.getGameId(),
          GameState.boot_in_progress));
    }
    catch (Exception e) {
      log.error("Jenkins failure to bootstrap game: " + game.getGameId());
      game.setState(GameState.boot_failed);
      throw e;
    }
  }

  /*
   * Look for bootable games. This means games that are 'boot_pending'.
   */
  public static void startBootableGames (List<Integer> runningExperimentIds,
                                         List<Game> notCompleteGames,
                                         List<Machine> freeMachines)
  {
    log.info("Looking for Bootable Games");

    List<Game> games =
        GamesScheduler.getBootableGames(runningExperimentIds, notCompleteGames);

    log.info(String.format("Found %s boot(s) ready to start", games.size()));

    for (Game game : games) {
      if (freeMachines.size() == 0) {
        log.info("No free machines, stop looking for Bootable Games");
        return;
      }

      log.info(String.format("Boot %s will be started ...", game.getGameId()));
      new RunBoot(game, freeMachines).run();
    }
  }
}