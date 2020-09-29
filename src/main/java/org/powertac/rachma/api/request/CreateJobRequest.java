package org.powertac.rachma.api.request;

import lombok.Getter;
import org.powertac.rachma.configuration.ConfigurationParameter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateJobRequest {

    @Getter
    private String name;

    @Getter
    private List<String> brokers;

    @Getter
    private Set<ConfigurationParameter> params = new HashSet<>();

}
