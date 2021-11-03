package org.powertac.rachma.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
public class WeatherConfigurationId implements Serializable {

    @Getter
    @Setter
    private String location;

    @Getter
    @Setter
    private Instant startTime;

}
