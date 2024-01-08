package org.powertac.orchestrator.broker;

public interface BrokerImageResolver {

    String getImageTag(Broker broker);

}
