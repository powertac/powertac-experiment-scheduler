package org.powertac.rachma.weather;

import java.util.Set;

public interface WeatherLocationRepository {

    Set<WeatherLocation> findAllLocations();

}
