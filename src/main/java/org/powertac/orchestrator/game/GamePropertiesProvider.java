package org.powertac.orchestrator.game;

import org.powertac.orchestrator.broker.Broker;

import java.util.Properties;

public interface GamePropertiesProvider {

    Properties getServerProperties(Game game);
    Properties getBrokerProperties(Game game, Broker broker);

}
