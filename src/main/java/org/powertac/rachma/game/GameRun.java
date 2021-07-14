package org.powertac.rachma.game;

import lombok.Getter;
import lombok.Setter;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.docker.network.DockerNetwork;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameRun {

    @Getter
    private final String id;

    @Getter
    private final Game game;

    @Getter
    @Setter
    private Instant start;

    @Getter
    @Setter
    private Instant end;

    @Setter
    @Getter
    private GameRunPhase phase;

    @Getter
    @Setter
    private boolean failed;

    @Setter
    @Getter
    private DockerContainer bootstrapContainer;

    @Setter
    @Getter
    private DockerNetwork network;

    @Setter
    @Getter
    private DockerContainer simulationContainer;

    @Getter
    @Setter
    private Map<Broker, DockerContainer> brokerContainers;

    public GameRun(String id, Game game) {
        this.id = id;
        this.game = game;
        this.phase = GameRunPhase.NONE;
        this.brokerContainers = new HashMap<>();
    }

    public boolean hasFailed() {
        return failed;
    }

    public Set<DockerContainer> getSimulationContainers() {
        Set<DockerContainer> containers = new HashSet<>();
        containers.add(simulationContainer);
        containers.addAll(brokerContainers.values());
        return containers;
    }

}
