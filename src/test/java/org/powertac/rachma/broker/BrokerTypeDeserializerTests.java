package org.powertac.rachma.broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testutils.TestHelper;

import java.io.IOException;

class BrokerTypeDeserializerTests {

    @Test
    void enabledBrokerTypeDeserializationTest() throws IOException {
        BrokerType type = getMapper().readValue(
            TestHelper.getContent("/enabled-broker-type.json"),
            BrokerType.class);

        Assertions.assertEquals("TestAgent", type.getName());
        Assertions.assertEquals("testagent:latest", type.getImage());
        Assertions.assertTrue(type.isEnabled());
    }

    @Test
    void disabledBrokerTypeDeserializationTest() throws IOException {
        BrokerType type = getMapper().readValue(
            TestHelper.getContent("/disabled-broker-type.json"),
            BrokerType.class);

        Assertions.assertEquals("TestAgent", type.getName());
        Assertions.assertEquals("testagent:latest", type.getImage());
        Assertions.assertFalse(type.isEnabled());
    }

    private ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BrokerType.class, new BrokerTypeDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

}
