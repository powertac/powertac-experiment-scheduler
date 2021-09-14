package org.powertac.rachma.game;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.docker.DockerContainer;
import org.powertac.rachma.docker.DockerNetwork;
import org.powertac.rachma.util.InstantToNumberSerializer;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
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
    @JsonSerialize(using = InstantToNumberSerializer.class)
    private Instant start;

    @Getter
    @Setter
    @JsonSerialize(using = InstantToNumberSerializer.class)
    private Instant end;

    @Setter
    @Getter
    private GameRunPhase phase;

    @Setter
    private boolean failed;

    @Setter
    @Getter
    @Transient
    @JsonIgnore
    private DockerContainer bootstrapContainer;

    @Setter
    @Getter
    @Transient
    @JsonIgnore
    private DockerNetwork network;

    @Setter
    @Getter
    @Transient
    @JsonIgnore
    private DockerContainer simulationContainer;

    @Getter
    @Setter
    @Transient
    @JsonIgnore
    private Map<Broker, DockerContainer> brokerContainers;

    public GameRun(String id, Game game) {
        this.id = id;
        this.game = game;
        this.phase = GameRunPhase.NONE;
        this.brokerContainers = new HashMap<>();
    }

    @JsonIgnore
    public Set<DockerContainer> getSimulationContainers() {
        Set<DockerContainer> containers = new HashSet<>();
        containers.add(simulationContainer);
        containers.addAll(brokerContainers.values());
        return containers;
    }

    public boolean hasFailed() {
        return this.failed;
    }

}
