package org.powertac.rachma.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.docker.container.DockerContainer;
import org.powertac.rachma.docker.network.DockerNetwork;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@NoArgsConstructor
public class GameRun {

    @Getter
    @Id
    @Column(length = 36)
    private String id;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    private Game game;

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
    @Transient
    private DockerContainer bootstrapContainer;

    @Setter
    @Getter
    @Transient
    private DockerNetwork network;

    @Setter
    @Getter
    @Transient
    private DockerContainer simulationContainer;

    @Getter
    @Setter
    @Transient
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
