package org.powertac.rachma.baseline;

import lombok.*;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.generator.GameGeneratorConfig;
import org.powertac.rachma.weather.WeatherConfiguration;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Baseline {

    @Id
    @Getter
    @Setter
    @Column(length = 36)
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "baseline_parameters", joinColumns = {@JoinColumn(name = "baseline_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "parameter", length = 128)
    @Column(name = "value")
    private Map<String, String> commonParameters;

    @Getter
    @Setter
    @OrderColumn
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<BrokerSet> brokerSets;

    @Getter
    @Setter
    @OrderColumn
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<WeatherConfiguration> weatherConfigurations;

    @Getter
    @Setter
    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "baseline")
    @OrderColumn
    private List<Game> games;

    @Getter
    @Setter
    private Instant createdAt;

    @Getter
    @Setter
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private GameGeneratorConfig config;

}
