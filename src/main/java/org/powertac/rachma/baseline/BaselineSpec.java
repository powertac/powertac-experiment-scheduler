package org.powertac.rachma.baseline;

import lombok.*;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.weather.WeatherConfiguration;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaselineSpec { // TODO : rename to GameSetGeneratorSpec or something

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Map<String, String> commonParameters;

    @Getter
    @Setter
    private List<BrokerSet> brokerSets;

    @Getter
    @Setter
    private List<WeatherConfiguration> weatherConfigurations;

}
