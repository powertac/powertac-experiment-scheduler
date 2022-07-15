package org.powertac.rachma.game;

import java.util.Collection;

public interface GameRepository {

    Collection<Game> findAll();
    Game findById(String id);
    Game findOneByName(String name);
    Game findFirstQueued();
    void save(Game game);
    boolean exists(String id);
    void delete(Game game);
}