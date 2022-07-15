package org.powertac.rachma.game;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.file.File;
import org.powertac.rachma.treatment.Treatment;
import org.powertac.rachma.util.InstantToNumberSerializer;
import org.powertac.rachma.weather.WeatherConfiguration;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
@Builder
public class Game {

    @Getter
    @Setter
    @Id
    @Column(length = 36)
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private BrokerSet brokerSet;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_server_parameters", joinColumns = {@JoinColumn(name = "game_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "parameter", length = 128)
    @Column(name = "value")
    @Builder.Default
    private Map<String, String> serverParameters = new HashMap<>();

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private File bootstrap; // TODO : make getter return Optional

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private File seed; // TODO : make getter return Optional

    @Getter
    @Setter
    @JsonSerialize(using = InstantToNumberSerializer.class)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<GameRun> runs = new ArrayList<>();

    @Getter
    @Setter
    @Builder.Default
    private boolean cancelled = false;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private WeatherConfiguration weatherConfiguration;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private Baseline baseline; // TODO : make getter return Optional

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private Treatment treatment; // TODO : make getter return Optional

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private Game base; // TODO : make getter return Optional

    // TODO : replace constructors with factory methods
    @Deprecated
    public Game(String id, String name, BrokerSet brokers, Map<String, String> serverParameters, File bootstrap, File seed, Instant createdAt) {
        this(id,
            name,
            brokers,
            serverParameters,
            bootstrap,
            seed,
            createdAt,
            new ArrayList<>(),
            false,
            null,
            null,
            null,
            null);
    }

    @Deprecated
    public Game(String id, String name, BrokerSet brokers, Map<String, String> serverParameters, File bootstrap, File seed, Instant createdAt, boolean cancelled) {
        this(id,
            name,
            brokers,
            serverParameters,
            bootstrap,
            seed,
            createdAt,
            new ArrayList<>(),
            cancelled,
            null,
            null,
            null,
            null);
    }

    @Deprecated
    public Game(String id, String name, BrokerSet brokers, Map<String, String> serverParameters, Instant createdAt, boolean cancelled) {
        this(id,
            name,
            brokers,
            serverParameters,
            null,
            null,
            createdAt,
            new ArrayList<>(),
            cancelled,
            null,
            null,
            null,
            null);
    }

    public Set<Broker> getBrokers() {
        return brokerSet.getBrokers();
    }

    @Transient
    public boolean isRunning() {
        return runs.stream()
            .map(GameRun::isRunning)
            .reduce(false, (oneIsRunning, currentOneIsRunning) -> oneIsRunning || currentOneIsRunning);
    }

    public GameRun getLatestSuccessfulRun() {
        return runs.stream().reduce(null, (current, run) ->
           run.wasSuccessful()
               ? null == current || run.getEnd().isAfter(current.getEnd()) ? run : current
               : null
        );
    }

}
