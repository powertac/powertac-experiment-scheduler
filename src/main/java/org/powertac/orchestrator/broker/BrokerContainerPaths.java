package org.powertac.orchestrator.broker;

import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;
import org.powertac.orchestrator.paths.PathProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BrokerContainerPaths implements PathProvider.ContainerPaths.BrokerPaths {

    private final static String basePath = "/powertac/broker";

    private final Broker broker;

    public BrokerContainerPaths(Broker broker) {
        this.broker = broker;
    }

    @Override
    public Path base() {
        return Paths.get(basePath);
    }

    @Override
    public GamePaths game(Game game) {
        // since the GamePaths interface has only one method at this time, it may be used as a function interface
        return () -> Paths.get(
            base().toString(),
            String.format("%s.%s.properties", game.getId(), broker.getName()));
    }

    @Override
    public GameRunPaths run(GameRun run) {
        return new GameRunPaths() {
            @Override public Path logs() {
                return Paths.get(base().toString(), "log");
            }
            @Override public Path data() {
                return Paths.get(base().toString(), "data");
            }
        };
    }

}
