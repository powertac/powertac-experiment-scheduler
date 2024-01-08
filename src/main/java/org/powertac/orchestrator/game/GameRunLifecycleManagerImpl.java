package org.powertac.orchestrator.game;

import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.docker.DockerContainer;
import org.powertac.orchestrator.docker.DockerNetwork;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class GameRunLifecycleManagerImpl implements GameRunLifecycleManager {

    private final GameRunRepository runs;
    private final GameRunLifecycleLogger logger;

    public GameRunLifecycleManagerImpl(GameRunRepository runs, GameRunLifecycleLogger logger) {
        this.runs = runs;
        this.logger = logger;
    }

    @Override
    public void preparation(GameRun run) {
        run.setStart(Instant.now());
        run.setPhase(GameRunPhase.PREPARATION);
        runs.save(run);
        logger.info(run, String.format("run[id=%s] is now preparing", run.getId()));
    }

    @Override
    public void bootstrap(GameRun run, DockerContainer container) {
        run.setPhase(GameRunPhase.BOOTSTRAP);
        run.setBootstrapContainer(container);
        runs.save(run);
        logger.info(run, String.format("run[id=%s] is now bootstrapping", run.getId()));
    }

    @Override
    public void ready(GameRun run) {
        run.setPhase(GameRunPhase.READY);
        runs.save(run);
        logger.info(run, String.format("run[id=%s] is now ready", run.getId()));
    }

    @Override
    public void simulation(GameRun run, DockerNetwork network, DockerContainer serverContainer, Map<Broker, DockerContainer> brokerContainers) {
        run.setPhase(GameRunPhase.SIMULATION);
        run.setNetwork(network);
        run.setSimulationContainer(serverContainer);
        run.setBrokerContainers(brokerContainers);
        runs.save(run);
        logger.info(run, String.format("run[id=%s] is now simulating", run.getId()));
    }

    @Override
    public void done(GameRun run) {
        run.setEnd(Instant.now());
        run.setPhase(GameRunPhase.DONE);
        runs.save(run);
        logger.info(run, String.format("run[id=%s] is done", run.getId()));
    }

    @Override
    public void fail(GameRun run) {
        persistFailedStatus(run);
        logger.error(run, String.format("run[id=%s] has failed", run.getId()));
    }

    @Override
    public void fail(GameRun run, Throwable error) {
        persistFailedStatus(run);
        logger.error(run, String.format("run[id=%s] has failed", run.getId()), error);
    }

    private void persistFailedStatus(GameRun run) {
        run.setEnd(Instant.now());
        run.setFailed(true);
        run.setPhase(GameRunPhase.DONE);
        runs.save(run);
    }

}
