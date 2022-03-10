package org.powertac.rachma.paths;

import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.OrchestratorGamePaths;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.game.OrchestratorGameRunPaths;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OrchestratorPathsImpl implements PathProvider.OrchestratorPaths {

    private final String basePath;

    public OrchestratorPathsImpl(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public Path games() {
        return Paths.get(basePath, "games");
    }

    @Override
    public Path brokers() {
        return Paths.get(basePath, "brokers");
    }

    @Override
    public GamePaths game(Game game) {
        return new OrchestratorGamePaths(this, game);
    }

    @Override
    public GameRunPaths run(GameRun run) {
        return new OrchestratorGameRunPaths(this, run);
    }
}
