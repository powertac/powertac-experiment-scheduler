package org.powertac.rachma.treatment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powertac.rachma.TestHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class TreatmentDeserializerTests {

    @Test
    void fixedServerParameterTreatmentDeserializationTest() throws IOException {
        Treatment deserializedTreatment = getMapper().readValue(
            TestHelper.getContent("/fixed-server-parameters-treatment.json"),
            Treatment.class);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("common.competition.simulationTimeslotSeconds", "1");
        Treatment treatment = new FixedServerParametersTreatment(parameters);

        Assertions.assertEquals(deserializedTreatment, treatment);
    }

    private ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Treatment.class, new TreatmentDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

}
