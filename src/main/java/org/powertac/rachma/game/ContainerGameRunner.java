package org.powertac.rachma.game;

import com.github.dockerjava.api.exception.DockerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerContainerCreator;
import org.powertac.rachma.docker.container.ContainerExitState;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.docker.container.DockerContainerController;
import org.powertac.rachma.docker.exception.ContainerException;
import org.powertac.rachma.docker.exception.KillContainerException;
import org.powertac.rachma.docker.network.DockerNetwork;
import org.powertac.rachma.docker.network.DockerNetworkRepository;
import org.powertac.rachma.server.BootstrapContainerCreator;
import org.powertac.rachma.server.SimulationContainerCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContainerGameRunner implements GameRunner {

    private final GameRunRepository runs;
    private final GameFileManager gameFileManager;
    private final BootstrapContainerCreator bootstrapContainerCreator;
    private final SimulationContainerCreator simulationContainerCreator;
    private final BrokerContainerCreator brokerContainerCreator;
    private final DockerContainerController controller;
    private final DockerNetworkRepository networks;
    private final GameRunLifecycleManager lifecycle;

    private final Map<Game, GameRun> activeRuns;
    private final Logger logger;

    @Autowired
    public ContainerGameRunner(GameRunRepository runs, GameFileManager gameFileManager,
                               BootstrapContainerCreator bootstrapContainerCreator,
                               SimulationContainerCreator simulationContainerCreator,
                               BrokerContainerCreator brokerContainerCreator,
                               DockerContainerController controller, DockerNetworkRepository networks,
                               GameRunLifecycleManager lifecycle) {
        this.runs = runs;
        this.gameFileManager = gameFileManager;
        this.bootstrapContainerCreator = bootstrapContainerCreator;
        this.simulationContainerCreator = simulationContainerCreator;
        this.brokerContainerCreator = brokerContainerCreator;
        this.controller = controller;
        this.networks = networks;
        this.lifecycle = lifecycle;
        this.activeRuns = new ConcurrentHashMap<>();
        logger = LogManager.getLogger(ContainerGameRunner.class);
    }

    @Override
    public void run(Game game) {
        logger.info(String.format("running game[id=%s]", game.getId()));
        GameRun run = runs.create(game);
        activeRuns.put(game, run);
        try {
            prepareGameFiles(run);
            bootstrap(run);
            simulate(run);
            lifecycle.done(run);
            logger.info(String.format("run for game[id=%s] completed successfully", game.getId()));
        } catch (Exception e) {
            lifecycle.fail(run);
            logger.error(String.format("run for game[id=%s] failed", game.getId()), e);
        } finally {
            activeRuns.remove(game);
        }
    }

    @PreDestroy
    public void shutdown() {
        for (GameRun run : activeRuns.values()) {
            stop(run);
            activeRuns.remove(run.getGame());
        }
    }

    private void prepareGameFiles(GameRun run) throws GameValidationException {
        try {
            lifecycle.preparation(run);
            gameFileManager.removeExisting(run.getGame());
            gameFileManager.createGameDirectories(run.getGame());
            gameFileManager.createSimulationProperties(run.getGame());
            for (Broker broker : run.getGame().getBrokers()) {
                gameFileManager.createBrokerProperties(run.getGame(), broker);
            }
        } catch (IOException e) {
            throw new GameValidationException("could not create game file", e);
        }
        if (!shouldBootstrap(run) && !gameFileManager.bootstrapExists(run.getGame())) {
            throw new GameValidationException("required bootstrap does not exist");
        }
        if (hasSeed(run) && !gameFileManager.seedExists(run.getGame())) {
            throw new GameValidationException("required seed does not exist");
        }
    }

    private boolean shouldBootstrap(GameRun run) {
        return null == run.getGame().getBootstrap();
    }

    private boolean hasSeed(GameRun run) {
        return null != run.getGame().getSeed();
    }

    private void bootstrap(GameRun run) throws GameRunException {
        if (shouldBootstrap(run)) {
            try {
                gameFileManager.createBootstrap(run.getGame());
                removeBootstrapContainerIfExists(run.getGame());
                DockerContainer bootstrapContainer = bootstrapContainerCreator.create(run.getGame());
                lifecycle.bootstrap(run, bootstrapContainer);
                ContainerExitState exitState = controller.run(run.getBootstrapContainer());
                if (exitState.isErrorState()) {
                    throw new GameRunException("failed to create bootstrap for game with id=" + run.getGame().getId());
                }
            } catch (IOException| ContainerException| DockerException e) {
                throw new GameRunException("failed to create bootstrap for game with id=" + run.getGame().getId(), e);
            }
        }
        lifecycle.ready(run);
    }

    private void removeBootstrapContainerIfExists(Game game) throws DockerException {
        String bootstrapContainerName = bootstrapContainerCreator.getBootstrapContainerName(game);
        if (controller.exists(bootstrapContainerName)) {
            controller.remove(bootstrapContainerName);
        }
    }

    private void simulate(GameRun run) throws GameRunException {
        try {
            // TODO : remove existing containers if necessary
            DockerNetwork network = createNetwork(run.getGame());
            DockerContainer serverContainer = simulationContainerCreator.create(run.getGame(), network);
            Map<Broker, DockerContainer> brokerContainers = createBrokerContainers(run.getGame(), network);
            lifecycle.simulation(run, network, serverContainer, brokerContainers);
            Map<DockerContainer, ContainerExitState> exitStates = controller.run(run.getSimulationContainers());
            for (ContainerExitState exitState : exitStates.values()) {
                if (exitState.isErrorState()) {
                    throw new GameRunException("a simulation container exited with an error code");
                }
            }
        } catch (ContainerException e) {
            throw new GameRunException("simulation run failed due to container error", e);
        } finally {
            if (null != run.getNetwork()) {
                networks.removeNetwork(run.getNetwork());
            }
        }
    }

    private DockerNetwork createNetwork(Game game) {
        return networks.createNetwork(String.format("ptac.%s", game.getId()));
    }

    private Map<Broker, DockerContainer> createBrokerContainers(Game game, DockerNetwork network) {
        Map<Broker, DockerContainer> brokerContainers = new HashMap<>();
        for (Broker broker : game.getBrokers()) {
            brokerContainers.put(broker, brokerContainerCreator.create(game, broker, network));
        }
        return brokerContainers;
    }

    private synchronized void stop(GameRun run) {
        try {
            if (run.getPhase().equals(GameRunPhase.BOOTSTRAP)) {
                controller.forceRemove(run.getBootstrapContainer());
            } else if (run.getPhase().equals(GameRunPhase.SIMULATION)) {
                for (DockerContainer container : run.getSimulationContainers()) {
                    controller.forceRemove(container);
                    networks.removeNetwork(run.getNetwork());
                }
            }
        } finally {
            lifecycle.fail(run);
        }
    }

}
