package org.powertac.rachma.baseline;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.broker.BrokerSet;
import org.powertac.rachma.weather.WeatherConfiguration;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class BaselineSpec {

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
