package org.powertac.rachma.game;

import lombok.Getter;
import org.powertac.rachma.weather.WeatherConfiguration;

import java.util.Map;
import java.util.Set;

public class NewGameDTO {

    @Getter
    private String name;

    @Getter
    private Set<String> brokerIds;

    @Getter
    private Map<String, String> parameters;

    @Getter
    private WeatherConfiguration weather;

}
