package org.powertac.orchestrator.broker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.orchestrator.validation.UUIDValidator;
import org.powertac.orchestrator.validation.exception.ValidationException;

import java.util.HashSet;
import java.util.Set;

public class BrokerSetFactoryTests {

    @Test
    void createNewSpecTest() throws ValidationException {
        Set<Broker> set = new HashSet<>();
        Broker broker1 = new Broker(
            "cd02f59e-02ac-478c-8234-93262d9e402a",
            "Klaus",
            "Santa",
            "klaus:santa",
            true);
        Broker broker2 = new Broker(
            "82bc7654-2622-47e0-8a7c-0ce5c4b63caf",
            "Martha",
            "Fogger",
            "martha:fogger",
            true);
        set.add(broker1);
        set.add(broker2);

        BrokerRepository repository = Mockito.mock(BrokerRepository.class);
        Mockito.when(repository.exists("cd02f59e-02ac-478c-8234-93262d9e402a")).thenReturn(true);
        Mockito.when(repository.exists("82bc7654-2622-47e0-8a7c-0ce5c4b63caf")).thenReturn(true);
        BrokerSetFactory factory = new BrokerSetFactoryImpl(repository);

        BrokerSet brokerSet = factory.create(set);
        Assertions.assertTrue(UUIDValidator.isValid(brokerSet.getId()));
        Assertions.assertTrue(brokerSet.getBrokers().contains(broker1));
        Assertions.assertTrue(brokerSet.getBrokers().contains(broker2));
    }

    @Test
    void validationFailsOnMissingBrokerTest() {
        Set<Broker> set = new HashSet<>();
        Broker broker1 = new Broker(
            "cd02f59e-02ac-478c-8234-93262d9e402a",
            "Klaus",
            "Santa",
            "klaus:santa",
            true);
        Broker broker2 = new Broker( // <- this one doesn't exist
            "82bc7654-2622-47e0-8a7c-0ce5c4b63caf",
            "Martha",
            "Fogger",
            "martha:fogger",
            true);
        set.add(broker1);
        set.add(broker2);

        BrokerRepository repository = Mockito.mock(BrokerRepository.class);
        Mockito.when(repository.exists("cd02f59e-02ac-478c-8234-93262d9e402a")).thenReturn(true);
        Mockito.when(repository.exists("82bc7654-2622-47e0-8a7c-0ce5c4b63caf")).thenReturn(false);
        BrokerSetFactory factory = new BrokerSetFactoryImpl(repository);
        Assertions.assertThrows(ValidationException.class, () -> factory.create(set));
    }

    @Test
    void validationFailsOnEmptySetTest() {
        BrokerRepository repository = Mockito.mock(BrokerRepository.class);
        BrokerSetFactory factory = new BrokerSetFactoryImpl(repository);
        Assertions.assertThrows(ValidationException.class, () -> factory.create(new HashSet<>()));
    }

}
