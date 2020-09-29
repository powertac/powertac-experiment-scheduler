package org.powertac.rachma.instance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.hash.HashProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InstanceHashProviderTests {

    @Test
    @SuppressWarnings("unchecked")
    void hashReturnsCorrectValueTest() {
        HashProvider<Broker> brokerHasher = (HashProvider<Broker>) Mockito.mock(HashProvider.class);
        HashProvider<Instance> hasher = new Sha256InstanceHashProvider(brokerHasher);

        Set<Broker> brokers = new HashSet<>();
        brokers.add(Mockito.mock(Broker.class));
        brokers.add(Mockito.mock(Broker.class));
        brokers.add(Mockito.mock(Broker.class));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("key", "value");
        parameters.put("real.key.for.real", "an even realer value");
        ServerParameters serverParameters = new TransientServerParameters(parameters);
        InstanceImpl instance = new InstanceImpl(brokers, serverParameters);

        hasher.getHash(instance);

        // TODO : what behaviour should be expected?
        Assertions.assertTrue(false);
    }

}
