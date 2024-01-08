package org.powertac.orchestrator.game;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.docker.DockerContainer;
import org.powertac.orchestrator.docker.DockerNetwork;
import org.powertac.orchestrator.util.InstantToNumberSerializer;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class GameRun implements Comparable<GameRun> {

    @Getter
    @Id
    @Column(length = 36)
    private String id;

    @Getter
    @Setter
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
    @Builder.Default
    private GameRunPhase phase = GameRunPhase.NONE;

    @Setter
    @Builder.Default
    private boolean failed = false;

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
    @Builder.Default
    private Map<Broker, DockerContainer> brokerContainers = new HashMap<>();

    public GameRun(String id, Game game) {
        this.id = id;
        this.game = game;
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

    @Transient
    public boolean isRunning() {
        return !phase.equals(GameRunPhase.NONE)
            && !phase.equals(GameRunPhase.DONE);
    }

    @Transient
    public boolean shouldBootstrap() {
        // FIXME : this is just a hotfix to always create the bootstrap
        // return null == this.getGame().getBootstrap();
        return true;
    }

    @Transient
    public boolean wasSuccessful() {
        return phase != null
            && phase.equals(GameRunPhase.DONE)
            && !failed;
    }

    @Override
    public int compareTo(GameRun run) {
        if (null == start) {
            return -1;
        } else if (null == run.getStart()) {
            return 1;
        } else if (this.start.equals(run.getStart())) {
            return 0;
        } else {
            return Comparator.comparing(GameRun::getStart).compare(this, run);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameRun gameRun = (GameRun) o;
        return getId() != null ? getId().equals(gameRun.getId()) : gameRun.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
