package org.powertac.rachma.weather;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.Instant;

@Entity
@NoArgsConstructor
@IdClass(WeatherConfigurationId.class)
public class WeatherConfiguration {

    @Id
    @Getter
    @Setter
    private String location;

    @Id
    @Getter
    @Setter
    private Instant startTime;

}
