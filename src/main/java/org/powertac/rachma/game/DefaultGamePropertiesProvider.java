package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

@Component
public class DefaultGamePropertiesProvider implements GamePropertiesProvider {

    @Value("${server.simulation.defaultPropertiesFile}")
    private String defaultServerPropertiesFile;

    @Value("${broker.defaultPropertiesFile}")
    private String defaultBrokerPropertiesFile;

    @Override
    public Properties getServerProperties(Game game) throws IOException {
        Properties properties = getDefaultServerProperties();
        for (Map.Entry<String, String> parameter : game.getServerParameters().entrySet()) {
            properties.setProperty(parameter.getKey(), parameter.getValue());
        }
        return properties;
    }

    @Override
    public Properties getBrokerProperties(Game game, Broker broker) throws IOException {
        Properties properties = getDefaultBrokerProperties();
        properties.setProperty("samplebroker.core.powerTacBroker.username", broker.getName());
        return properties;
    }

    private Properties getDefaultServerProperties() throws IOException {
        return readPropertiesFile(Paths.get(defaultServerPropertiesFile));
    }

    private Properties getDefaultBrokerProperties() throws IOException {
        return readPropertiesFile(Paths.get(defaultBrokerPropertiesFile));
    }

    private Properties readPropertiesFile(Path path) throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newInputStream(path));
        return properties;
    }

}
