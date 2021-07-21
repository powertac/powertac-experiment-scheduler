package org.powertac.rachma.broker;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LocalStorageBrokerSeederImpl implements BrokerSeeder {

    private final BrokerRepository brokers;
    private final BrokerTypeRepository types;

    public LocalStorageBrokerSeederImpl(BrokerRepository brokers, BrokerTypeRepository types) {
        this.brokers = brokers;
        this.types = types;
    }

    @Override
    public void seedBrokers() {
        for (BrokerType type : types.findAll().values()) {
            if (!brokers.exists(type.getName(), "latest")) {
                String id = UUID.randomUUID().toString();
                Broker broker = new Broker(id, type.getName(), "latest", null);
                brokers.save(broker);
            }
        }
    }

}
