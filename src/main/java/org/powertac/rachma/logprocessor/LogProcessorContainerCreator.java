package org.powertac.rachma.logprocessor;

import org.powertac.rachma.docker.DockerContainer;
import org.powertac.rachma.game.Game;

public interface LogProcessorContainerCreator {

    DockerContainer create(Game game);

}
