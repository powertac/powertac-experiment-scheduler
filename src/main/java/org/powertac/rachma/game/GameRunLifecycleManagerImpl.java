package org.powertac.rachma.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.docker.network.DockerNetwork;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class GameRunLifecycleManagerImpl implements GameRunLifecycleManager {

    private final GameRunRepository runs;

    private final Logger logger;

    public GameRunLifecycleManagerImpl(GameRunRepository runs) {
        this.runs = runs;
        logger = LogManager.getLogger(GameRunLifecycleManagerImpl.class);
    }

    @Override
    public void preparation(GameRun run) {
        run.setStart(Instant.now());
        run.setPhase(GameRunPhase.PREPARATION);
        runs.save(run);
        logger.info(String.format("run[id=%s] is now preparing", run.getId()));
    }

    @Override
    public void bootstrap(GameRun run, DockerContainer container) {
        run.setPhase(GameRunPhase.BOOTSTRAP);
        run.setBootstrapContainer(container);
        runs.save(run);
        logger.info(String.format("run[id=%s] is now bootstrapping", run.getId()));
    }

    @Override
    public void ready(GameRun run) {
        run.setPhase(GameRunPhase.READY);
        runs.save(run);
        logger.info(String.format("run[id=%s] is now ready", run.getId()));
    }

    @Override
    public void simulation(GameRun run, DockerNetwork network, DockerContainer serverContainer, Map<Broker, DockerContainer> brokerContainers) {
        run.setPhase(GameRunPhase.SIMULATION);
        run.setNetwork(network);
        run.setSimulationContainer(serverContainer);
        run.setBrokerContainers(brokerContainers);
        runs.save(run);
        logger.info(String.format("run[id=%s] is now simulating", run.getId()));
    }

    @Override
    public void done(GameRun run) {
        run.setEnd(Instant.now());
        run.setPhase(GameRunPhase.DONE);
        runs.save(run);
        logger.info(String.format("run[id=%s] is done", run.getId()));
    }

    @Override
    public void fail(GameRun run) {
        run.setEnd(Instant.now());
        run.setFailed(true);
        run.setPhase(GameRunPhase.DONE);
        runs.save(run);
        logger.error(String.format("run[id=%s] has failed", run.getId()));
    }

}
