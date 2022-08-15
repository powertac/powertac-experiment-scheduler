package org.powertac.rachma.game;

import java.util.Collection;

public interface GameRunRepository {

    GameRun find(String id);
    GameRun create(Game game);
    Collection<GameRun> findFailed();
    void save(GameRun run);
    void delete(Collection<GameRun> runs);

}
