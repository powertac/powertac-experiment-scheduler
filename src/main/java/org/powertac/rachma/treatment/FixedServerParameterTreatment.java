package org.powertac.rachma.treatment;

import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.instance.ServerParameters;

import java.util.Objects;

@Deprecated
public class FixedServerParameterTreatment implements Treatment {

    private final String key;
    private final String value;

    public FixedServerParameterTreatment(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Instance mutate(Instance instanceCopy) {
        instanceCopy
            .getServerParameters()
            .set(key, value);
        return instanceCopy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixedServerParameterTreatment that = (FixedServerParameterTreatment) o;
        return Objects.equals(key, that.key) &&
            Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

}
