package org.powertac.orchestrator.broker;

import org.powertac.orchestrator.validation.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public BrokerSet createFromIds(Set<String> ids) throws ValidationException {
        Set<Broker> brokers = ids.stream()
            .map(brokerRepository::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        if (brokers.size() != ids.size()) {
            throw new ValidationException(ids, "could not resolve one or more brokers");
        } else {
            return create(brokers);
        }
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
