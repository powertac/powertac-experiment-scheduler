package org.powertac.rachma.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.file.File;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
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
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Broker> brokers;

    @Getter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_server_parameters", joinColumns = {@JoinColumn(name = "game_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "parameter", length = 128)
    @Column(name = "value")
    private Map<String, String> serverParameters;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    private File bootstrap;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    private File seed;

    @Getter
    private Instant createdAt;

    @Getter
    @OneToMany(fetch = FetchType.LAZY)
    private List<GameRun> runs;

}
