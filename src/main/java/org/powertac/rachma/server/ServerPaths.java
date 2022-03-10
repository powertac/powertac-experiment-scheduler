package org.powertac.rachma.server;

import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.game.ServerGamePaths;
import org.powertac.rachma.game.ServerGameRunPaths;
import org.powertac.rachma.paths.PathProvider;

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
