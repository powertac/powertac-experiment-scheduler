package org.powertac.rachma.file;

import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;

import java.io.IOException;
import java.nio.file.Path;

public interface GameArchiveBuilder {
    void buildArchive(Game game) throws IOException;
    void buildArchive(GameRun run, Path output) throws IOException;
}
