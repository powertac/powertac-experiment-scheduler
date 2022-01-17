package org.powertac.rachma.game;

import java.util.Collection;

public interface GameRunRepository {

    GameRun find(String id);
    GameRun create(Game game);
    void save(GameRun run);
    void delete(Collection<GameRun> runs);

}
