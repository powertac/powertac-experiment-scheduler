package org.powertac.rachma.game;

public interface GameScheduler {

    void schedule(Game game);
    void runScheduledGames() throws InterruptedException;

}
