package org.powertac.orchestrator.artifact;

import org.powertac.orchestrator.game.Game;

public interface ArtifactResolver {

    boolean has(Game game, ArtifactProducer producer);

}
