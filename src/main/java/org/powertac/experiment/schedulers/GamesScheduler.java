package org.powertac.experiment.schedulers;

import org.powertac.experiment.beans.Game;
import org.powertac.experiment.states.GameState;

import java.util.ArrayList;
import java.util.List;


public class GamesScheduler
{
  public static List<Game> getBootableGames (List<Integer> runningExperimentIds,
                                             List<Game> notCompleteGames)
  {
    List<Game> games = new ArrayList<>();
    List<Game> lastGames = new ArrayList<>();

    for (Game game : notCompleteGames) {
      if (!game.getState().equals(GameState.boot_pending)) {
        continue;
      }
      // First boot for the running experiments
      if (runningExperimentIds != null && runningExperimentIds.size() > 0 &&
          !runningExperimentIds.contains(game.getExperiment().getExperimentId())) {
        lastGames.add(game);
      }
      else {
        games.add(game);
      }
    }

    games.addAll(lastGames);

    return games;
  }

  /*
   * This function returns a list of all startable games in order of urgency.
   * the urgency of a game is the sum of the startable games of all brokers in
   * that game. This favors the bigger games (more brokers) since they'll have
   * a higher sum, and it favors the games with brokers that still have a
   * lot of games to do.
   */
  @SuppressWarnings("unchecked")
  public static List<Game> getStartableGames (List<Integer> runningExperimentIds,
                                              List<Game> notCompleteGames)
  {
    List<Game> games = new ArrayList<>();

    if (runningExperimentIds == null || runningExperimentIds.size() == 0) {
      return games;
    }

    for (Game game : notCompleteGames) {
      if (game.getState() == GameState.boot_complete &&
          runningExperimentIds.contains(game.getExperiment().getExperimentId())) {
        games.add(game);
      }
    }

    return games;
  }
}