package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;

import java.io.IOException;
import java.util.Properties;

public interface GamePropertiesProvider {

    Properties getServerProperties(Game game);
    Properties getBrokerProperties(Game game, Broker broker);

}