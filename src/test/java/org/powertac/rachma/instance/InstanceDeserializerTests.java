package org.powertac.rachma.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powertac.rachma.TestHelper;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerDeserializer;
import org.powertac.rachma.broker.BrokerImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class InstanceDeserializerTests {

    @Test
    void brokersAreDeserializedCorrectlyTest() throws IOException {
        Instance instance = getMapper().readValue(
            TestHelper.getContent("/instance.json"),
            Instance.class);

        Set<Broker> brokers = instance.getBrokers();

        Map<String, String> udeConfig = new HashMap<>();
        udeConfig.put("hans", "1234");
        udeConfig.put("other", "55586");
        Broker ude = new BrokerImpl("AgentUDE", "2015", udeConfig);
        Broker ewi = new BrokerImpl("EWIIS3", "2020.1");
        Broker crocodile = new BrokerImpl("CrocodileAgent", "latest");

        Assertions.assertEquals(3, brokers.size());
        Assertions.assertTrue(brokers.contains(ude));
        Assertions.assertTrue(brokers.contains(ewi));
        Assertions.assertTrue(brokers.contains(crocodile));
    }

    @Test
    void serverParametersAreDeserializedCorrectlyTest() throws IOException {
        Instance instance = getMapper().readValue(
            TestHelper.getContent("/instance.json"),
            Instance.class);

        Map<String, String> params = new HashMap<>();
        params.put("accounting.accountingService.bankInterest", "0.05");
        params.put("common.competition.simulationBaseTime", "2010-06-06");
        ServerParameters serverParameters = new TransientServerParameters(params);

        Assertions.assertEquals(serverParameters, instance.getServerParameters());
    }

    private ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Instance.class, new InstanceDeserializer());
        module.addDeserializer(Broker.class, new BrokerDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

}
