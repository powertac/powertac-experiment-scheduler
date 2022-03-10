package org.powertac.rachma.baseline;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testutils.TestHelper;

import java.io.IOException;

public class BaselineDeserializationTests {

    @Test
    void deserializeTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules().configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        Baseline baseline = mapper.readValue(TestHelper.getContent("/baseline/baseline.json"), Baseline.class);
        Assertions.assertEquals("32651e8c-9fb2-436c-8d09-da2081ca0205", baseline.getId());
        Assertions.assertEquals("Test Baseline", baseline.getName());
        Assertions.assertEquals(2, baseline.getBrokerSets().size());
        Assertions.assertEquals(2, baseline.getWeatherConfigurations().size());
        Assertions.assertNull(baseline.getGames());
    }

}
