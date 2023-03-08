package org.powertac.rachma.treatment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.powertac.rachma.broker.Broker;

import javax.persistence.*;
import java.util.Map;

@Entity
@NoArgsConstructor
@JsonDeserialize(using = ModifierDeserializer.class)
public class ReplaceBrokerModifier extends Modifier {

    @Getter
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "broker_mapping",
        joinColumns = {@JoinColumn(name = "modifier_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "replacement_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "original_id")
    private Map<Broker, Broker> brokerMapping;

    public final ModifierType getType() {
        return ModifierType.REPLACE_BROKER;
    }

    public ReplaceBrokerModifier(String id, String name, Map<Broker, Broker> brokerMapping) {
        this.setId(id);
        this.setName(name);
        this.brokerMapping = brokerMapping;
    }

}
