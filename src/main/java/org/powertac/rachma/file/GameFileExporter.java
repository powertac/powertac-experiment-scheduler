package org.powertac.rachma.file;

import org.powertac.rachma.game.Game;

import java.io.IOException;
import java.util.List;

public interface GameFileExporter {

    void exportGames(List<Game> games, String targetRoot, String hostUri) throws IOException;

}
