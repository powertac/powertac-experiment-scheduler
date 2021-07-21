package org.powertac.rachma.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Broker {

    @Id
    @Column(length = 36)
    private String id;

    @Getter
    private String name;

    @Getter
    private String version;

    @Getter
    @Transient
    private Map<String, String> config;

    public Broker(String name, String version) {
        this.name = name;
        this.version = version;
        this.config = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Broker broker = (Broker) o;
        if (id != null ? !id.equals(broker.id) : broker.id != null) return false;
        if (!getName().equals(broker.getName())) return false;
        return getVersion().equals(broker.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getVersion(), getConfig());
    }
}
