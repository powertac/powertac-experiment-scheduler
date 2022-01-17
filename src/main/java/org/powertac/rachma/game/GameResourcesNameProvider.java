package org.powertac.rachma.game;

import java.util.Collection;

public interface GameResourcesNameProvider {

    String simulationContainerName(Game game);
    String simulationNetworkName(Game game);
    Collection<String> brokerContainerNames(Game game);

}
