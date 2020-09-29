package org.powertac.rachma.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class BrokerImpl implements Broker {

    @Getter
    private String name;

    @Getter
    private String version;

    @Getter
    private Map<String, String> config;

    public BrokerImpl(String name, String version) {
        this.name = name;
        this.version = version;
        this.config = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // TODO : compare on the broker interface level ()
        if (o == null || getClass() != o.getClass()) return false;
        BrokerImpl broker = (BrokerImpl) o;
        return getName().equals(broker.getName()) &&
            getVersion().equals(broker.getVersion()) &&
            Objects.equals(getConfig(), broker.getConfig());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getVersion(), getConfig());
    }
}
