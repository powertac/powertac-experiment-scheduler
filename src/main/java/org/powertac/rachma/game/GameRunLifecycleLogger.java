package org.powertac.rachma.game;

public interface GameRunLifecycleLogger {

    void info(GameRun run, String message);
    void error(GameRun run, String message);
    void error(GameRun run, String message, Throwable error);

}
