package org.powertac.orchestrator.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Broker {

    @Id
    @Getter
    @Setter
    @Column(length = 36)
    private String id;

    @Getter
    private String name;

    @Getter
    private String version;

    @Getter
    @Setter
    private String imageTag;

    @Getter
    @Setter
    private boolean enabled;

    public String getHumanReadableIdentifier() {
        return String.format("%s__%s", this.name, this.version);
    }

    public Broker(String name, String version) {
        this.name = name;
        this.version = version;
        this.enabled = false;
        this.imageTag = "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Broker broker = (Broker) o;
        if (isEnabled() != broker.isEnabled()) return false;
        if (getId() != null ? !getId().equals(broker.getId()) : broker.getId() != null) return false;
        if (!getName().equals(broker.getName())) return false;
        if (!getVersion().equals(broker.getVersion())) return false;
        return getImageTag() != null ? getImageTag().equals(broker.getImageTag()) : broker.getImageTag() == null;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getVersion() != null ? getVersion().hashCode() : 0);
        return result;
    }
}
