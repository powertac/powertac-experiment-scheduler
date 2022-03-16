package org.powertac.rachma.baseline;

import org.powertac.rachma.game.Game;

import java.util.Optional;

public interface BaselineRepository {

    Iterable<Baseline> findAll();
    void save(Baseline baseline);
    String getBaselineIdByGame(Game game);
    Optional<Baseline> findByName(String name);
    void delete(Baseline baseline);

}
