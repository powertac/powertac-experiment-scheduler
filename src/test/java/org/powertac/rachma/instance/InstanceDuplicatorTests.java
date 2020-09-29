package org.powertac.rachma.instance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerDuplicator;
import org.powertac.rachma.util.IdProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class InstanceDuplicatorTests {

    @Test
    void createCopyTest() {
        IdProvider idProvider = Mockito.mock(IdProvider.class);
        BrokerDuplicator brokerDuplicator = Mockito.mock(BrokerDuplicator.class);
        InstanceDuplicator duplicator = new InstanceDuplicatorImpl(idProvider, brokerDuplicator);

        String id = "123456abcdef";

        Broker broker1 = Mockito.mock(Broker.class);
        Broker broker2 = Mockito.mock(Broker.class);
        Broker broker3 = Mockito.mock(Broker.class);
        Set<Broker> brokers = Stream.of(broker1, broker2, broker3).collect(Collectors.toSet());

        Broker broker1Copy = Mockito.mock(Broker.class);
        Broker broker2Copy = Mockito.mock(Broker.class);
        Broker broker3Copy = Mockito.mock(Broker.class);
        Mockito.when(brokerDuplicator.createCopy(broker1)).thenReturn(broker1Copy);
        Mockito.when(brokerDuplicator.createCopy(broker2)).thenReturn(broker2Copy);
        Mockito.when(brokerDuplicator.createCopy(broker3)).thenReturn(broker3Copy);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("important.value", "12345");
        parameters.put("some.value", "Petra");
        ServerParameters serverParameters = new TransientServerParameters(parameters);

        Instance instance = new InstanceImpl(id, brokers, serverParameters);
        Instance instanceCopy = duplicator.createCopy(instance);

        Assertions.assertNotSame(instance, instanceCopy);
        Assertions.assertNotSame(instance.getId(), instanceCopy.getId());
        Assertions.assertNotEquals(id, instanceCopy.getId());
        Assertions.assertNotSame(instance.getServerParameters(), instanceCopy.getServerParameters());
        Assertions.assertEquals(instance.getServerParameters(), instanceCopy.getServerParameters());
        Assertions.assertEquals(3, instanceCopy.getBrokers().size());
        Assertions.assertTrue(instanceCopy.getBrokers().contains(broker1Copy));
        Assertions.assertTrue(instanceCopy.getBrokers().contains(broker2Copy));
        Assertions.assertTrue(instanceCopy.getBrokers().contains(broker3Copy));
    }

}
