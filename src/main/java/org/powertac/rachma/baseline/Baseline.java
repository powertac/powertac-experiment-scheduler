package org.powertac.rachma.baseline;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.weather.WeatherConfiguration;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
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
    @CollectionTable(name = "baseline_spec_parameters", joinColumns = {@JoinColumn(name = "baseline_spec_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "parameter", length = 128)
    @Column(name = "value")
    private Map<String, String> commonParameters;

    @Getter
    @Setter
    @OrderColumn
    @ManyToMany
    private List<BrokerSet> brokerSets;

    @Getter
    @Setter
    @OrderColumn
    @ManyToMany
    private List<WeatherConfiguration> weatherConfigurations;

    @Getter
    @Setter
    @ManyToMany
    @OrderColumn
    private List<Game> games;

}
