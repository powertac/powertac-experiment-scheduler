package org.powertac.orchestrator.baseline;

import org.powertac.orchestrator.game.Game;

import java.util.List;

public interface BaselineGameFactory {

    List<Game> createGames(Baseline baseline);
    List<Game> createGames(BaselineSpec spec);

}
