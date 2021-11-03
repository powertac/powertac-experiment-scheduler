package org.powertac.rachma.game;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.file.File;
import org.powertac.rachma.util.InstantToNumberSerializer;
import org.powertac.rachma.weather.WeatherConfiguration;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @ManyToOne
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

    public Set<Broker> getBrokers() {
        return brokerSet.getBrokers();
    }

}
