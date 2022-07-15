package org.powertac.rachma.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BrokerSet {

    @Id
    @Getter
    @Setter
    @Column(length = 36)
    private String id;

    @Getter
    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Broker> brokers = new HashSet<>();

    public BrokerSet addBroker(Broker broker) {
        brokers.add(broker);
        return this;
    }

}