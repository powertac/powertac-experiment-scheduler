package org.powertac.rachma.game;

import java.util.Collection;

public interface GameRunRepository {

    GameRun create(Game game);
    void save(GameRun run);
    Collection<GameRun> findActiveByGame(Game game);
    boolean hasSuccessfulRun(Game game);
    boolean hasActiveRun(Game game);

}
