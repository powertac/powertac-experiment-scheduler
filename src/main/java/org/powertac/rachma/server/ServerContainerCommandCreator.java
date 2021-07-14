package org.powertac.rachma.server;

import java.util.List;
import java.util.Set;

public interface ServerContainerCommandCreator {

    List<String> createBootstrapCommand(String propertiesFilePath, String bootstrapFilePath);
    List<String> createSimulationCommand(String propertiesFilePath, String bootstrapFilePath, String seedFilePath, Set<String> brokerNames);

}
