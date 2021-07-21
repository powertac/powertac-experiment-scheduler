package org.powertac.rachma.broker;

import org.powertac.rachma.persistence.JpaBrokerRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class PersistentBrokerRepository implements BrokerRepository {

    private final JpaBrokerRepository brokers;

    public PersistentBrokerRepository(JpaBrokerRepository brokers) {
        this.brokers = brokers;
    }

    @Override
    public Collection<Broker> findAll() {
        List<Broker> brokerList = new ArrayList<>();
        brokers.findAll().forEach(brokerList::add);
        return brokerList;
    }

    @Override
    public Broker findById(String id) {
        return brokers.findById(id).orElse(null);
    }

    @Override
    public Broker findByName(String name) {
        return brokers.findByName(name);
    }

    @Override
    public boolean exists(String name, String version) {
        return brokers.existsByNameAndVersion(name, version);
    }

    @Override
    public void save(Broker broker) {
        // TODO : add constraint that only one name/version combination must exist at a given time
        brokers.save(broker);
    }

}
