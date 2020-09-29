package org.powertac.rachma.instance;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerDuplicator;
import org.powertac.rachma.util.IdProvider;
import org.springframework.stereotype.Component;

import java.util.*;
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
            copyBrokers(instance.getBrokers()),
            copyServerParameters(instance.getServerParameters()));
    }

    private Set<Broker> copyBrokers(Set<Broker> brokers) {
        return brokers.stream()
            .map(brokerDuplicator::createCopy)
            .collect(Collectors.toSet());
    }

    private ServerParameters copyServerParameters(ServerParameters serverParameters) {
        return new TransientServerParameters(
            new HashMap<>(
                serverParameters.getParameters()));
    }
}
