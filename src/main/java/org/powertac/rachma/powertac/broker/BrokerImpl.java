package org.powertac.rachma.powertac.broker;

import lombok.Getter;
import org.powertac.rachma.broker.BrokerType;

public class BrokerImpl implements Broker {

    @Getter
    private final String id;

    @Getter
    private final String name;

    @Getter
    private final BrokerType type;

    public BrokerImpl(String id, String name, BrokerType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

}
