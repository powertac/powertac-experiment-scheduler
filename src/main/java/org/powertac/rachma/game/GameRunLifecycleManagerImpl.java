package org.powertac.rachma.game;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.docker.network.DockerNetwork;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class GameRunLifecycleManagerImpl implements GameRunLifecycleManager {

    private final GameRunRepository runs;

    public GameRunLifecycleManagerImpl(GameRunRepository runs) {
        this.runs = runs;
    }

    @Override
    public void preparation(GameRun run) {
        run.setStart(Instant.now());
        run.setPhase(GameRunPhase.PREPARATION);
        runs.update(run);
    }

    @Override
    public void bootstrap(GameRun run, DockerContainer container) {
        run.setPhase(GameRunPhase.BOOTSTRAP);
        run.setBootstrapContainer(container);
        runs.update(run);
    }

    @Override
    public void ready(GameRun run) {
        run.setPhase(GameRunPhase.READY);
        runs.update(run);
    }

    @Override
    public void simulation(GameRun run, DockerNetwork network, DockerContainer serverContainer, Map<Broker, DockerContainer> brokerContainers) {
        run.setPhase(GameRunPhase.SIMULATION);
        run.setNetwork(network);
        run.setSimulationContainer(serverContainer);
        run.setBrokerContainers(brokerContainers);
        runs.update(run);
    }

    @Override
    public void done(GameRun run) {
        run.setEnd(Instant.now());
        run.setPhase(GameRunPhase.DONE);
        runs.update(run);
    }

    @Override
    public void fail(GameRun run) {
        run.setEnd(Instant.now());
        run.setFailed(true);
        run.setPhase(GameRunPhase.DONE);
        runs.update(run);
    }

}
