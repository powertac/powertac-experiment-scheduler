package org.powertac.orchestrator.server;

import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;
import org.powertac.orchestrator.game.ServerGamePaths;
import org.powertac.orchestrator.game.ServerGameRunPaths;
import org.powertac.orchestrator.paths.PathProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerPaths implements PathProvider.ContainerPaths.ServerPaths {

    private final static String basePath = "/powertac/server";

    @Override
    public Path base() {
        return Paths.get(basePath);
    }

    @Override
    public GamePaths game(Game game) {
        return new ServerGamePaths(this, game);
    }

    @Override
    public GameRunPaths run(GameRun run) {
        return new ServerGameRunPaths(this, run);
    }

}
