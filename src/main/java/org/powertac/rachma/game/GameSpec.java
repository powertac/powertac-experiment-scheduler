package org.powertac.rachma.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.weather.WeatherConfiguration;

import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameSpec {

    @Getter
    private String name;

    @Getter
    private String baseGameId;

    @Getter
    private Set<Broker> brokers;

    @Getter
    private Map<String, String> serverParameters;

    @Getter
    private WeatherConfiguration weather;

}
