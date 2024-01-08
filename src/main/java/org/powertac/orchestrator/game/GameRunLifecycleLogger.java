package org.powertac.orchestrator.game;

public interface GameRunLifecycleLogger {

    void info(GameRun run, String message);
    void error(GameRun run, String message);
    void error(GameRun run, String message, Throwable error);

}
