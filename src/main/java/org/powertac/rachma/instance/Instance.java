package org.powertac.rachma.instance;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.runner.RunnableEntity;

import java.util.Set;

public interface Instance extends RunnableEntity {

    String getId();

    ServerParameters getServerParameters();

    Set<Broker> getBrokers();

}
