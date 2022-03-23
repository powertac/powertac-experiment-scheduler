package org.powertac.rachma.paths;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;
import org.powertac.rachma.util.Versioned;

import java.nio.file.Path;

public interface PathProvider extends Versioned {

    @Override
    default String getVersion() {
        return "1.0";
    }

    OrchestratorPaths host();
    OrchestratorPaths local();
    ContainerPaths container();

    interface OrchestratorPaths {
        Path games();
        @Deprecated Path brokers(); // deprecated due to manual broker management; automated image build will be removed
        GamePaths game(Game game);
        GameRunPaths run(GameRun run);

        interface GamePaths {
            Path bootstrap();
            Path dir();
            Path runs();
            Path properties();
            Path seed();
            BrokerPaths broker(Broker broker);

            interface BrokerPaths {
                Path properties();
            }
        }

        interface GameRunPaths {
            Path dir();
            Path log();
            Path serverLogs();
            Path state();
            Path trace();
            BrokerPaths broker(Broker broker);

            interface BrokerPaths {
                Path dir();
            }
        }
    }

    interface ContainerPaths {
        ServerPaths server();
        BrokerPaths broker(Broker broker);

        interface ServerPaths {
            Path base();
            GamePaths game(Game game);
            GameRunPaths run(GameRun run);

            interface GamePaths {
                Path bootstrap();
                Path properties();
                Path seed();
            }

            interface GameRunPaths {
                Path state();
                Path trace();
            }
        }

        interface BrokerPaths {
            Path base();
            GamePaths game(Game game);
            GameRunPaths run(GameRun run);

            interface GamePaths {
                Path properties();
            }

            interface GameRunPaths {
                Path logs();
            }
        }
    }

}
