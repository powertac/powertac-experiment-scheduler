package org.powertac.rachma.broker;

import org.powertac.rachma.validation.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class BrokerSetFactoryImpl implements BrokerSetFactory {

    private final BrokerRepository brokerRepository;

    public BrokerSetFactoryImpl(BrokerRepository brokerRepository) {
        this.brokerRepository = brokerRepository;
    }

    @Override
    public BrokerSet create(Set<Broker> brokers) throws ValidationException {
        validateSetSize(brokers);
        validateBrokerExistence(brokers);
        return new BrokerSet(
            UUID.randomUUID().toString(),
            new HashSet<>(brokers));
    }

    private void validateSetSize(Set<Broker> brokers) throws ValidationException {
        if (brokers.size() < 1) {
            throw new ValidationException(brokers, "Broker set must include at least one broker");
        }
    }

    private void validateBrokerExistence(Set<Broker> brokers) throws ValidationException {
        for (Broker broker : brokers) {
            if (!brokerRepository.exists(broker.getId())) {
                throw new ValidationException(broker, String.format("broker[id=%s] does not exist", broker.getId()));
            }
        }
    }

}
