package org.powertac.rachma.game;

import com.github.dockerjava.api.exception.DockerException;
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

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class ContainerGameRunner implements GameRunner {

    private final GameRunRepository runs;
    private final GameFileManager gameFileManager;
    private final BootstrapContainerCreator bootstrapContainerCreator;
    private final SimulationContainerCreator simulationContainerCreator;
    private final BrokerContainerCreator brokerContainerCreator;
    private final DockerContainerController controller;
    private final DockerNetworkRepository networks;

    @Autowired
    public ContainerGameRunner(GameRunRepository runs, GameFileManager gameFileManager,
                               BootstrapContainerCreator bootstrapContainerCreator,
                               SimulationContainerCreator simulationContainerCreator,
                               BrokerContainerCreator brokerContainerCreator,
                               DockerContainerController controller, DockerNetworkRepository networks) {
        this.runs = runs;
        this.gameFileManager = gameFileManager;
        this.bootstrapContainerCreator = bootstrapContainerCreator;
        this.simulationContainerCreator = simulationContainerCreator;
        this.brokerContainerCreator = brokerContainerCreator;
        this.controller = controller;
        this.networks = networks;
    }

    @Override
    public void run(Game game) {
        GameRun run = runs.create(game);
        run.setStart(Instant.now());
        runs.update(run);
        try {
            prepareGameFiles(run);
            bootstrap(run);
            simulate(run);
        } catch (GameRunException|GameValidationException e) {
            // TODO : add game event logging
            run.setFailed(true);
        } finally {
            run.setEnd(Instant.now());
            run.setPhase(GameRunPhase.DONE);
            runs.update(run);
        }
    }

    @Override
    public void stop(Game game) {
        for (GameRun run : runs.findActiveByGame(game)) {
            stop(run);
        }
    }

    private void prepareGameFiles(GameRun run) throws GameValidationException {
        try {
            run.setPhase(GameRunPhase.PREPARATION);
            runs.update(run);
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
                run.setPhase(GameRunPhase.BOOTSTRAP);
                run.setBootstrapContainer(bootstrapContainerCreator.create(run.getGame()));
                runs.update(run);
                ContainerExitState exitState = controller.run(run.getBootstrapContainer());
                if (exitState.isErrorState()) {
                    run.setFailed(true);
                    runs.update(run);
                    throw new GameRunException("failed to create bootstrap for game with id=" + run.getGame().getId());
                }
            } catch (ContainerException| DockerException e) {
                throw new GameRunException("failed to create bootstrap for game with id=" + run.getGame().getId(), e);
            }
        }
        run.setPhase(GameRunPhase.READY);
        runs.update(run);
    }

    private void simulate(GameRun run) throws GameRunException {
        try {
            run.setPhase(GameRunPhase.SIMULATION);
            run.setNetwork(createNetwork(run.getGame()));
            run.setSimulationContainer(simulationContainerCreator.create(run.getGame(), run.getNetwork()));
            run.setBrokerContainers(createBrokerContainers(run.getGame(), run.getNetwork()));
            runs.update(run);
            Map<DockerContainer, ContainerExitState> exitStates = controller.run(run.getSimulationContainers());
            for (ContainerExitState exitState : exitStates.values()) {
                if (exitState.isErrorState()) {
                    run.setFailed(true);
                }
            }
        } catch (ContainerException e) {
            throw new GameRunException("simulation run failed due to container error", e);
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

    private void stop(GameRun run) {
        try {
            if (run.getPhase().equals(GameRunPhase.BOOTSTRAP)) {
                controller.kill(run.getBootstrapContainer());
                return;
            }
            if (run.getPhase().equals(GameRunPhase.SIMULATION)) {
                controller.kill(run.getSimulationContainer());
                for (DockerContainer brokerContainer : run.getBrokerContainers().values()) {
                    controller.kill(brokerContainer);
                }
            }
        } catch (KillContainerException e) {
            // TODO : add to list of orphaned resources and log
        } finally {
            run.setFailed(true);
            run.setPhase(GameRunPhase.DONE);
        }
    }

}
