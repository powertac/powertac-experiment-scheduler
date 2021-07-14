package org.powertac.rachma.broker;

import lombok.AllArgsConstructor;
import org.powertac.rachma.file.PathContext;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GamePathProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

@AllArgsConstructor
public class BrokerPathProvider {

    private final PathContext context;
    private final Game game;
    private final Broker broker;

    public Path dir() {
        switch (context.getType()) {
            case HOST:
            case LOCAL:
                return Paths.get(new GamePathProvider(context, game).brokers().toString(), broker.getName());
            case CONTAINER:
                return Paths.get(context.getRoot(), "broker");
        }
        return null;
    }

    public Path properties() {
        return Paths.get(dir().toString(), String.format("%s.%s.properties", game.getId(), broker.getName()));
    }

    public Path shared() {
        return Paths.get(dir().toString(), "shared");
    }

}
