package org.powertac.rachma.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
public class WeatherConfigurationId implements Serializable {

    @Getter
    @Setter
    private String location;

    @Getter
    @Setter
    private Instant startTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherConfigurationId that = (WeatherConfigurationId) o;
        if (!getLocation().equals(that.getLocation())) return false;
        return getStartTime().equals(that.getStartTime());
    }

    @Override
    public int hashCode() {
        int result = getLocation().hashCode();
        result = 31 * result + getStartTime().hashCode();
        return result;
    }
}
