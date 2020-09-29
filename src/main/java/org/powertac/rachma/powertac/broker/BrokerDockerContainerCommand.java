package org.powertac.rachma.powertac.broker;

import org.powertac.rachma.docker.DockerContainerCommand;

import java.util.List;

public class BrokerDockerContainerCommand implements DockerContainerCommand {

    private final List<String> options;

    public BrokerDockerContainerCommand(List<String> options) {
        this.options = options;
    }

    @Override
    public List<String> toList() {
        return options;
    }
    
}
