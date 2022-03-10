package org.powertac.rachma.paths;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerContainerPaths;

public class ContainerPathsImpl implements PathProvider.ContainerPaths {

    @Override
    public ServerPaths server() {
        return new org.powertac.rachma.server.ServerPaths();
    }

    @Override
    public BrokerPaths broker(Broker broker) {
        return new BrokerContainerPaths(broker);
    }

}
