package org.powertac.rachma.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.time.Instant;

// TODO : make embeddable to reduce number of joins on game

@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(WeatherConfigurationId.class)
public class WeatherConfiguration {

    @Id
    @Getter
    @Setter
    @Column(length = 128)
    private String location;

    @Id
    @Getter
    @Setter
    private Instant startTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherConfiguration that = (WeatherConfiguration) o;
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
