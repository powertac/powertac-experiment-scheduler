package org.powertac.rachma.game;

import org.powertac.rachma.paths.PathProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerGameRunPaths implements PathProvider.ContainerPaths.ServerPaths.GameRunPaths {

    private final PathProvider.ContainerPaths.ServerPaths parent;
    private final GameRun run;

    public ServerGameRunPaths(PathProvider.ContainerPaths.ServerPaths parent, GameRun run) {
        this.parent = parent;
        this.run = run;
    }

    @Override
    public Path state() {
        return Paths.get(
            logs().toString(),
            "powertac-sim-0.state");
    }

    @Override
    public Path trace() {
        return Paths.get(
            logs().toString(),
            "powertac-sim-0.trace");
    }

    private Path logs() {
        return Paths.get(
            parent.base().toString(),
            "log");
    }

}
