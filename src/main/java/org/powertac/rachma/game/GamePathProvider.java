package org.powertac.rachma.game;

import lombok.AllArgsConstructor;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerPathProvider;
import org.powertac.rachma.file.PathContext;
import org.powertac.rachma.file.PathContextType;

import java.nio.file.Path;
import java.nio.file.Paths;

@AllArgsConstructor
public class GamePathProvider {

    private final PathContext context;
    private final Game game;

    public BrokerPathProvider broker(Broker broker) {
        return new BrokerPathProvider(context, game, broker);
    }

    public Path dir() {
        if (context.getType().equals(PathContextType.CONTAINER)) {
            return null;
        }
        return Paths.get(new ContextPathProvider(context).gamesDir().toString(), game.getId()).normalize();
    }

    public Path brokers() {
        if (context.getType().equals(PathContextType.CONTAINER)) {
            return null;
        }
        return Paths.get(dir().toString(), "brokers").normalize();
    }

    public Path logs() {
        if (context.getType().equals(PathContextType.CONTAINER)) {
            return Paths.get("/powertac-server/log");
        }
        return Paths.get(dir().toString(), "log");
    }

    public Path stateLog() {
        return Paths.get(logs().toString(), String.format("%s.state", game.getId()));
    }

    public Path traceLog() {
        return Paths.get(logs().toString(), String.format("%s.trace", game.getId()));
    }

    public Path properties() {
        String propertiesFileFormat = "%s.simulation.properties";
        if (context.getType().equals(PathContextType.CONTAINER)) {
            return Paths.get(context.getRoot(), String.format(propertiesFileFormat, game.getId()));
        } else {
            return Paths.get(dir().toString(), String.format(propertiesFileFormat, game.getId()));
        }
    }

    public Path bootstrap() {
        if (null == game.getBootstrap()) {
            return getDefaultBootstrapPath();
        } else {
            return new GamePathProvider(context, game.getBootstrap().getGame()).bootstrap();
        }
    }

    public Path seed() {
        if (null == game.getSeed()) {
            return null;
        }
        if (context.getType().equals(PathContextType.CONTAINER)) {
            return Paths.get(context.getRoot(), String.format("%s.state", game.getSeed().getGame().getId()));
        }
        // TODO : enforce non-transitivity
        return new GamePathProvider(context, game.getSeed().getGame()).stateLog();
    }

    private Path getDefaultBootstrapPath() {
        String bootstrapFileFormat = "%s.bootstrap.xml";
        if (context.getType().equals(PathContextType.CONTAINER)) {
            return Paths.get(context.getRoot(), String.format(bootstrapFileFormat, game.getId()));
        } else {
            return Paths.get(dir().toString(), String.format(bootstrapFileFormat, game.getId()));
        }
    }

}
