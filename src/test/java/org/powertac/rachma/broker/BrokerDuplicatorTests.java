package org.powertac.rachma.broker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class BrokerDuplicatorTests {

    @Test
    void createBrokerCopyTest() {
        BrokerDuplicator duplicator = new BrokerDuplicatorImpl();

        Map<String, String> brokerParameters = new HashMap<>();
        brokerParameters.put("broker.parameter.one", "this is value");
        brokerParameters.put("lord.of.the.things", "mithrandir");

        Broker broker = new BrokerImpl("BrokerName", "v1.0.0", brokerParameters);

        Broker brokerCopy = duplicator.createCopy(broker);

        Assertions.assertNotSame(broker, brokerCopy);
        Assertions.assertNotSame(broker.getName(), brokerCopy.getName());
        Assertions.assertEquals(broker.getName(), brokerCopy.getName());
        Assertions.assertNotSame(broker.getVersion(), brokerCopy.getVersion());
        Assertions.assertEquals(broker.getVersion(), brokerCopy.getVersion());
        Assertions.assertNotSame(broker.getConfig(), brokerCopy.getConfig());
        Assertions.assertEquals(broker.getConfig(), brokerCopy.getConfig());
    }

}
