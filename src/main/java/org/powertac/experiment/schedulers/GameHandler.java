package org.powertac.experiment.schedulers;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.powertac.experiment.beans.Agent;
import org.powertac.experiment.beans.Game;
import org.powertac.experiment.beans.Machine;
import org.powertac.experiment.services.MemStore;
import org.powertac.experiment.services.Utils;
import org.powertac.experiment.states.AgentState;
import org.powertac.experiment.states.GameState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;


public class GameHandler
{
  private static Logger log = Utils.getLogger();

  private Game game;
  private int gameId;

  public GameHandler (Game game)
  {
    this.game = game;
    this.gameId = game.getGameId();
  }

  public void handleStatus (Session session, String status)
  {
    GameState newState = GameState.valueOf(status);
    if (newState.equals(game.getState())) {
      return;
    }

    game.setState(newState);
    log.info(String.format("Update game: %s to %s", gameId, status));

    switch (newState) {
      case boot_in_progress:
        break;

      case boot_complete:
        // Reset values for aborted games
        for (Agent agent : game.getAgentMap().values()) {
          agent.setState(AgentState.pending);
          agent.setBalance(0);
          session.update(agent);
        }
        MemStore.removeGameInfo(gameId);

        // Duplicate boot files for other games in this experiment
        boolean reuse = game.getExperiment().getParamMap().getReuseBoot();
        boolean first = "1".equals(game.getGameName().replaceAll(".*_", ""));
        if (reuse && first) {
          for (Game otherGame : game.getExperiment().getGameMap().values()) {
            if (otherGame.getGameId().equals(game.getGameId())) {
              continue;
            }

            try {
              Files.copy(new File(game.getBootLocation()).toPath(),
                  new File(otherGame.getBootLocation()).toPath());
              log.info(String.format("Copied boot for game: %s ", otherGame.getGameName()));
            }
            catch (IOException ioe) {
              ioe.printStackTrace();
            }
          }
        }

        break;

      case boot_failed:
        log.warn("BOOT " + gameId + " FAILED!");
        break;

      case game_ready:
        break;

      case game_in_progress:
        break;

      case game_complete:
        for (Agent agent : game.getAgentMap().values()) {
          agent.setState(AgentState.complete);
          session.update(agent);
        }
        log.info("Setting Agents to Complete for game: " + gameId);
        // If all games of round are complete, set round complete
        game.getExperiment().gameCompleted(session, gameId);
        MemStore.removeGameInfo(gameId);
        break;

      case game_failed:
        log.warn("GAME " + gameId + " FAILED!");
        for (Agent agent : game.getAgentMap().values()) {
          agent.setState(AgentState.complete);
          session.update(agent);
        }
        log.info("Setting Agents to Complete for game: " + gameId);
        MemStore.removeGameInfo(gameId);
        break;
    }

    if (GameState.freeMachine.contains(newState)) {
      Machine.delayedMachineUpdate(game.getMachine(), 10);
      game.setMachine(null);
      log.debug("Freeing Machine for game: " + gameId);
    }
    session.update(game);
  }

  /*
   * This is called when the REST interface receives a heartbeat message (GET)
   * or a end-of-game message (POST)
  */
  public String handleStandings (Session session, String standings,
                                 boolean isEndOfGame) throws Exception
  {
    log.debug("We received standings for game " + gameId);

    if (isEndOfGame) {
      log.debug("Status of the game is " + game.getState());

      if (!game.getState().isRunning()) {
        session.getTransaction().rollback();
        log.warn("Game is not running, ignoring!");
        return "error";
      }
    }

    HashMap<String, Double> results = new HashMap<>();
    for (String result : standings.split(",")) {
      Double balance = Double.parseDouble(result.split(":")[1]);
      String name = result.split(":")[0];
      if (name.equals("default broker")) {
        continue;
      }
      results.put(name, balance);
    }

    for (Agent agent : game.getAgentMap().values()) {
      Double balance = results.get(agent.getBroker().getBrokerName());
      if (balance == null || balance == Double.NaN) {
        continue;
      }
      agent.setBalance(balance);
      session.update(agent);
    }

    session.getTransaction().commit();
    return "success";
  }
}
