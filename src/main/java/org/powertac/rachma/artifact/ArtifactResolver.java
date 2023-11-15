package org.powertac.rachma.artifact;

import org.powertac.rachma.game.Game;

public interface ArtifactResolver {

    boolean has(Game game, ArtifactProducer producer);

}
