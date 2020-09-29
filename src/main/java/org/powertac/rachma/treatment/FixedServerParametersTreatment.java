package org.powertac.rachma.treatment;

import lombok.Getter;
import org.powertac.rachma.instance.Instance;

import java.util.Map;
import java.util.Objects;

public class FixedServerParametersTreatment implements Treatment {

    @Getter
    private Map<String, String> parameters;

    public FixedServerParametersTreatment(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Instance mutate(Instance instanceCopy) {
        Map<String, String> existingParameters = instanceCopy.getServerParameters().getParameters();
        existingParameters.putAll(parameters);
        return instanceCopy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedServerParametersTreatment that = (FixedServerParametersTreatment) o;
        return getParameters().equals(that.getParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParameters());
    }

}
