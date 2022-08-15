package org.powertac.rachma.file;

import org.powertac.rachma.game.GameRun;

import java.io.IOException;
import java.nio.file.Path;

public interface GameArchiveBuilder {
    void buildArchive(GameRun run, Path output) throws IOException;
}
