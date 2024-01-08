package org.powertac.orchestrator.paths;

import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.broker.BrokerContainerPaths;

public class ContainerPathsImpl implements PathProvider.ContainerPaths {

    @Override
    public ServerPaths server() {
        return new org.powertac.orchestrator.server.ServerPaths();
    }

    @Override
    public BrokerPaths broker(Broker broker) {
        return new BrokerContainerPaths(broker);
    }

}
