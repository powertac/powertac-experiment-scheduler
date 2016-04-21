package org.powertac.experiment.jobs;

import org.apache.log4j.Logger;
import org.powertac.experiment.beans.Agent;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Machine;
import org.powertac.experiment.states.GameState;
import org.powertac.experiment.services.JenkinsConnector;
import org.powertac.experiment.services.Properties;
import org.powertac.experiment.services.Utils;


public class RunAgent
{
  private static Logger log = Utils.getLogger();

  private Properties properties = Properties.getProperties();

  private Machine machine;
  private Game game;
  private Agent agent;

  public RunAgent (Agent agent, Game game, Machine machine)
  {
    this.machine = machine;
    this.game = game;
    this.agent = agent;
  }

  public void run () throws Exception
  {
    log.info("Running RunAgent for broker " + agent.getBrokerId());

    String finalUrl =
        properties.getProperty("jenkins.location")
            + "job/start-agent/buildWithParameters?"
            + "experimentUrl=" + properties.getProperty("tourneyUrl")
            + "&brokerId=" + agent.getBrokerId()
            + "&gameName=" + game.getGameName()
            + "&gameId=" + game.getGameId()
            + "&machine=" + machine.getMachineName();

    log.info("Final url: " + finalUrl);

    try {
      JenkinsConnector.sendJob(finalUrl);
      log.info(String.format("Jenkins request to start broker: %s for game: %s",
          agent.getBrokerId(), game.getGameId()));
      agent.setMachine(machine);
    }
    catch (Exception e) {
      log.error(String.format("Jenkins failure to start broker: %s for game: %s",
          agent.getBrokerId(), game.getGameId()));
      game.setState(GameState.game_failed);
      throw e;
    }
  }
}