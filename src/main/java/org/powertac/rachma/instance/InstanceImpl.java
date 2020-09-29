package org.powertac.rachma.instance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.broker.Broker;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class InstanceImpl implements Instance {

    @Getter
    @Setter
    private String id;

    @Getter
    private Set<Broker> brokers;

    @Getter
    private ServerParameters serverParameters;

    public InstanceImpl(Set<Broker> brokers, ServerParameters serverParameters) {
        this.brokers = brokers;
        this.serverParameters = serverParameters;
    }

}
