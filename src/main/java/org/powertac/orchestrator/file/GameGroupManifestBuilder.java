package org.powertac.orchestrator.file;

import org.powertac.orchestrator.game.Game;

import java.util.List;

public interface GameGroupManifestBuilder {

    String buildManifest(List<Game> games, String hostUri, String delimiter);

}
