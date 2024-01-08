package org.powertac.orchestrator.baseline;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testutils.TestHelper;
import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.weather.WeatherConfiguration;

import java.io.IOException;
import java.time.Instant;

public class BaselineSpecDeserializationTests {

    @Test
    void standardDeserializeSpecTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules().configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        BaselineSpec spec = mapper.readValue(TestHelper.getContent("/baseline/baseline-spec.json"), BaselineSpec.class);

        // name
        Assertions.assertEquals("Spec Test", spec.getName());

        // common properties
        Assertions.assertEquals(2, spec.getCommonParameters().size());
        Assertions.assertEquals("some value", spec.getCommonParameters().get("some.parameter"));
        Assertions.assertEquals("5", spec.getCommonParameters().get("some.number"));

        // broker sets
        Assertions.assertEquals(2, spec.getBrokerSets().size());
        Assertions.assertTrue(spec.getBrokerSets().get(0).getBrokers().contains(new Broker(
            "de041ebe-d323-4ecb-ad0a-6b212237c871",
            "Sample Broker",
            "latest",
            "samplebroker:latest",
            true)));
        Assertions.assertTrue(spec.getBrokerSets().get(0).getBrokers().contains(new Broker(
            "91e6afb1-2998-4d2c-a6ed-3312193d6a9b",
            "Another Broker",
            "latest",
            "another:latest",
            true)));
        Assertions.assertTrue(spec.getBrokerSets().get(1).getBrokers().contains(new Broker(
            "de041ebe-d323-4ecb-ad0a-6b212237c871",
            "Sample Broker",
            "latest",
            "samplebroker:latest",
            true)));

        // weather configs
        Assertions.assertEquals(2, spec.getWeatherConfigurations().size());
        Assertions.assertEquals(spec.getWeatherConfigurations().get(0), new WeatherConfiguration(
            "rotterdam",
            Instant.parse("2011-12-01T00:00:00Z")));
        Assertions.assertEquals(spec.getWeatherConfigurations().get(1), new WeatherConfiguration(
            "cheyenne",
            Instant.parse("2013-01-01T00:00:00Z")));
    }

}
