package org.powertac.rachma.baseline;

import org.powertac.rachma.game.Game;

public interface BaselineRepository {

    Iterable<Baseline> findAll();
    void save(Baseline baseline);
    String getBaselineIdByGame(Game game);

}
