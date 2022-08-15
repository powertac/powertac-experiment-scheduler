package org.powertac.rachma.file;

import org.powertac.rachma.game.Game;

import java.util.List;

public interface GameGroupManifestBuilder {

    String getManifest(List<Game> games, String hostUri);

}
