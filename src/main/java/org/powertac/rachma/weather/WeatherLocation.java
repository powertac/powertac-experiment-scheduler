package org.powertac.rachma.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
public class WeatherLocation {

    @Getter
    private String name;

    @Getter
    private Instant minReportTime;

    @Getter
    private Instant maxReportTime;

    @Getter
    private Instant minForecastTime;

    @Getter
    private Instant maxForecastTime;

    public boolean includesDate(Instant date) {
        return date.isAfter(minReportTime.minus(1, ChronoUnit.SECONDS))
            && date.isBefore(maxReportTime.plus(1, ChronoUnit.SECONDS))
            && date.isAfter(minForecastTime.minus(1, ChronoUnit.SECONDS))
            && date.isBefore(maxForecastTime.plus(1, ChronoUnit.SECONDS));
    }

}
