package org.powertac.rachma.broker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
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
    @ManyToMany
    private Set<Broker> brokers;

}
