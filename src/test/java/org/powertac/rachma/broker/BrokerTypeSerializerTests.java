package org.powertac.rachma.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powertac.rachma.powertac.broker.serialization.BrokerTypeSerializer;

class BrokerTypeSerializerTests {

    @Test
    void serializeEnabledBrokerTypeTest() throws JsonProcessingException {
        BrokerType type = new BrokerTypeImpl(
            "TestAgent",
            "testagent:latest");
        String json = getMapper().writeValueAsString(type);

        Assertions.assertEquals(
            "{\"name\":\"TestAgent\",\"image\":\"testagent:latest\",\"disabled\":false}",
            json);
    }

    @Test
    void serializeDisabledBrokerTypeTest() throws JsonProcessingException {
        BrokerType type = new BrokerTypeImpl(
            "TestAgent",
            "testagent:latest",
            false);
        String json = getMapper().writeValueAsString(type);

        Assertions.assertEquals(
            "{\"name\":\"TestAgent\",\"image\":\"testagent:latest\",\"disabled\":true}",
            json);
    }

    private ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(BrokerType.class, new BrokerTypeSerializer());
        mapper.registerModule(module);
        return mapper;
    }

}
