package org.powertac.rachma.instance;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerDuplicator;
import org.powertac.rachma.util.IdProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InstanceDuplicatorImpl implements InstanceDuplicator {

    private final IdProvider idProvider;
    private final BrokerDuplicator brokerDuplicator;

    public InstanceDuplicatorImpl(IdProvider idProvider, BrokerDuplicator brokerDuplicator) {
        this.idProvider = idProvider;
        this.brokerDuplicator = brokerDuplicator;
    }

    @Override
    public Instance createCopy(Instance instance) {
        return new InstanceImpl(
            idProvider.getAnyId(),
            instance.getName(),
            copyBrokers(instance.getBrokers()),
            copyServerParameters(instance.getServerParameters()),
            instance.getBootstrapFile(),
            instance.getSeedFile());
    }

    @Override
    // TODO : add test
    public Instance createNamedCopy(String name, Instance instance) {
        return new InstanceImpl(
            idProvider.getAnyId(),
            name,
            copyBrokers(instance.getBrokers()),
            copyServerParameters(instance.getServerParameters()),
            instance.getBootstrapFile(),
            instance.getSeedFile());
    }

    private Set<Broker> copyBrokers(Set<Broker> brokers) {
        return brokers.stream()
            .map(brokerDuplicator::createCopy)
            .collect(Collectors.toSet());
    }

    private ServerParameters copyServerParameters(ServerParameters serverParameters) {
        return new TransientServerParameters(new HashMap<>(serverParameters.getParameters()));
    }

}
