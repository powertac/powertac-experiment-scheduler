package org.powertac.orchestrator.artifact;

import org.powertac.orchestrator.game.Game;
import org.springframework.stereotype.Component;

@Component
public class FileSystemArtifactResolver implements ArtifactResolver {

    @Override
    public boolean has(Game game, ArtifactProducer producer) {
        return false;
    }



}
