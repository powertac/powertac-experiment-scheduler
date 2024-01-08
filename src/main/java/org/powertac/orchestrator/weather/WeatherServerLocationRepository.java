package org.powertac.orchestrator.weather;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class WeatherServerLocationRepository implements WeatherLocationRepository {

    private final RestTemplateBuilder rest;

    public WeatherServerLocationRepository(RestTemplateBuilder rest) {
        this.rest = rest;
    }

    @Override
    public Set<WeatherLocation> findAllLocations() {
        return rest.build().exchange(
            "http://weatherserver/locations/",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Set<WeatherLocation>>() {}
        ).getBody();
    }

}
