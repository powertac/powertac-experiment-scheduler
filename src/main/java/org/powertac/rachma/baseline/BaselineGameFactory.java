package org.powertac.rachma.baseline;

import org.powertac.rachma.game.Game;

import java.util.List;

public interface BaselineGameFactory {

    List<Game> createGames(Baseline baseline);
    List<Game> createGames(BaselineSpec spec);

}
