package org.powertac.rachma.broker;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class BrokerDuplicatorImpl implements BrokerDuplicator {

    @Override
    public Broker createCopy(Broker broker) {
        return new BrokerImpl(
            new String(broker.getName()),
            new String(broker.getVersion()),
            new HashMap<>(broker.getConfig()));
    }

}
