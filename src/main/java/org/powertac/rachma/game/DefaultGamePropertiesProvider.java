package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Component
public class DefaultGamePropertiesProvider implements GamePropertiesProvider {

    @Override
    public Properties getServerProperties(Game game) {
        Properties properties = getDefaultServerProperties();
        for (Map.Entry<String, String> parameter : game.getServerParameters().entrySet()) {
            properties.setProperty(parameter.getKey(), parameter.getValue());
        }
        return properties;
    }

    @Override
    public Properties getBrokerProperties(Game game, Broker broker) {
        Properties properties = getDefaultBrokerProperties();
        properties.setProperty("samplebroker.core.powerTacBroker.username", broker.getName());
        return properties;
    }

    private Properties getDefaultServerProperties() {
        Properties defaultProperties = new Properties();
        defaultProperties.put("server.mode", "research");
        defaultProperties.put("server.competitionControlService.brokerPauseAllowed", true);
        defaultProperties.put("server.competitionControlService.loginTimeout", 60000);
        defaultProperties.put("server.jmsManagementService.jmsBrokerUrl", "tcp://0.0.0.0:61616");
        return defaultProperties;
    }

    private Properties getDefaultBrokerProperties() {
        Properties defaultProperties = new Properties();
        defaultProperties.put("samplebroker.core.jmsManagementService.jmsBrokerUrl", "tcp://powertac-server:61616");
        defaultProperties.put("samplebroker.core.powerTacBroker.retryTimeLimit", 60000);
        return defaultProperties;
    }

}
