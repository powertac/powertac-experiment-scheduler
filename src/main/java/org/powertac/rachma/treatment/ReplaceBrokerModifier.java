package org.powertac.rachma.treatment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.NoArgsConstructor;
import org.powertac.rachma.broker.Broker;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@NoArgsConstructor
@JsonDeserialize(using = ModifierDeserializer.class)
public class ReplaceBrokerModifier extends Modifier {

    /*
     * This whole approach (manually mapping entities to ids) is a workaround since Hibernate created the wrong
     * key constraints (made replacement_id, a foreign key, unique; therefore every broker could only used as a
     * replacement once).
     *
     * TODO : Long-term this has to be replaced with correct entity mappings; this requires working database migrations
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "replace_modifier_broker_mapping", joinColumns = {@JoinColumn(name = "modifier_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "original_id", length = 128)
    @Column(name = "replacement_id")
    private Map<String, String> brokerIdMapping = new HashMap<>();

    @ManyToMany
    @JoinTable(name = "modifier_to_brokers")
    private Set<Broker> brokers = new HashSet<>();

    @Transient
    private Map<Broker, Broker> brokerEntityMapping;

    public final ModifierType getType() {
        return ModifierType.REPLACE_BROKER;
    }

    public synchronized Map<Broker, Broker> getBrokerMapping() {
        if (null == brokerEntityMapping) {
            HashMap<Broker, Broker> entityMapping = new HashMap<>();
            for (Map.Entry<String, String> mapping : brokerIdMapping.entrySet()) {
                entityMapping.put(findBroker(mapping.getKey()), findBroker(mapping.getValue()));
            }
            brokerEntityMapping = entityMapping;
        }
        return brokerEntityMapping;
    }

    public ReplaceBrokerModifier(String id, String name, Map<Broker, Broker> brokerMapping) {
        this.setId(id);
        this.setName(name);
        this.brokerEntityMapping = brokerMapping;
        for (Map.Entry<Broker, Broker> mapping : brokerMapping.entrySet()) {
            Broker original = mapping.getKey();
            Broker replacement = mapping.getValue();
            brokers.add(original);
            brokers.add(replacement);
            this.brokerIdMapping.put(original.getId(), replacement.getId());
        }
    }

    private Broker findBroker(String id) {
        for (Broker broker : brokers) {
            if (broker.getId().equals(id)) {
                return broker;
            }
        }
        return null;
    }

}
