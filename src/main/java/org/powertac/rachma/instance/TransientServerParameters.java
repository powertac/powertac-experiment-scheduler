package org.powertac.rachma.instance;

import lombok.NoArgsConstructor;
import org.powertac.rachma.configuration.ConfigurationParameter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class TransientServerParameters implements ServerParameters {

    private Map<String, String> parameters;

    public TransientServerParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public boolean has(String key) {
        return parameters.containsKey(key);
    }

    @Override
    public void set(String key, String value) {
        parameters.put(key, value);
    }

    @Override
    public Set<ConfigurationParameter> toConfigurationParameterSet() {
        return parameters.entrySet().stream()
            .map(entry -> new ConfigurationParameter(
                entry.getKey(),
                entry.getValue()))
            .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransientServerParameters that = (TransientServerParameters) o;
        return getParameters().equals(that.getParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParameters());
    }
}
