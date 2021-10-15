package org.powertac.rachma.game;

import org.powertac.rachma.file.PathContext;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GameRunPathProvider {

    private final PathContext context;
    private final GameRun run;

    public GameRunPathProvider(PathContext context, GameRun run) {
        this.context = context;
        this.run = run;
    }

    public Path log() {
        return Paths.get(
            new GamePathProvider(context, run.getGame()).dir().toString(),
            String.format("%s.run.log", run.getId()));
    }

}
