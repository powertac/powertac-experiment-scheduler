package org.powertac.orchestrator.file;

import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;

import java.io.IOException;
import java.nio.file.Path;

public interface GameArchiveBuilder {
    void buildArchive(Game game) throws IOException;
    void buildArchive(GameRun run, Path output) throws IOException;
}
