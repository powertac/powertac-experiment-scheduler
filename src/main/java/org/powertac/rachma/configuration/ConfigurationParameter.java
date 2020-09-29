package org.powertac.rachma.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class ConfigurationParameter {

    @Getter
    private final String parameter;

    @Getter
    private final String value;

    @JsonCreator
    public ConfigurationParameter(@JsonProperty("parameter") String parameter, @JsonProperty("value") String value) {
        this.parameter = parameter;
        this.value = value;
    }

}
