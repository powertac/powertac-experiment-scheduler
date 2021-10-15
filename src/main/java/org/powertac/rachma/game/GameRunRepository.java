package org.powertac.rachma.game;

public interface GameRunRepository {

    GameRun find(String id);
    GameRun create(Game game);
    void save(GameRun run);

}
