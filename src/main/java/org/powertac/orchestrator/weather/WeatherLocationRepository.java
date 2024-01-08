package org.powertac.orchestrator.weather;

import java.util.Set;

public interface WeatherLocationRepository {

    Set<WeatherLocation> findAllLocations();

}
