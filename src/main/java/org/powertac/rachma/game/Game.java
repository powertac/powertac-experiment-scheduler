package org.powertac.rachma.game;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.file.File;
import org.powertac.rachma.util.InstantToNumberSerializer;
import org.powertac.rachma.weather.WeatherConfiguration;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Game {

    @Getter
    @Setter
    @Id
    @Column(length = 36)
    private String id;

    @Getter
    private String name;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private BrokerSet brokerSet;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_server_parameters", joinColumns = {@JoinColumn(name = "game_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "parameter", length = 128)
    @Column(name = "value")
    private Map<String, String> serverParameters;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private File bootstrap;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private File seed;

    @Getter
    @JsonSerialize(using = InstantToNumberSerializer.class)
    private Instant createdAt;

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameRun> runs = new ArrayList<>();

    @Getter
    @Setter
    private boolean cancelled = false;

    @Getter
    @Setter
    @ManyToOne
    private WeatherConfiguration weatherConfiguration;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private Baseline baseline;

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
            null);
    }

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
            null);
    }

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
            null);
    }

    public Set<Broker> getBrokers() {
        return brokerSet.getBrokers();
    }

    @Transient
    public boolean isRunning() {
        return runs.stream().map(GameRun::isRunning)
            .reduce(false, (oneIsRunning, currentOneIsRunning) -> oneIsRunning || currentOneIsRunning);
    }

}
