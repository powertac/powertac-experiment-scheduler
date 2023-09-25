package org.powertac.rachma.game;

import lombok.Builder;
import lombok.Getter;
import org.powertac.rachma.weather.WeatherConfiguration;

import java.util.Map;
import java.util.Set;

@Builder
public class GameConfigDTO {

    @Getter
    private Set<String> brokerIds;

    @Getter
    private Map<String, String> parameters;

    @Getter
    private WeatherConfiguration weather;

    @Getter
    private String seed;

}
