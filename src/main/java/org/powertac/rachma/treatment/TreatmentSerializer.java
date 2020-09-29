package org.powertac.rachma.treatment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.instance.Parameter;
import org.powertac.rachma.instance.ParameterImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TreatmentSerializer extends StdSerializer<Treatment> {

    public TreatmentSerializer() {
        super(Treatment.class);
    }

    @Override
    public void serialize(Treatment treatment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (treatment != null) {
            jsonGenerator.writeStartObject();
            if (treatment instanceof BrokerMutationTreatment) {
                serializeBrokerMutationTreatment(jsonGenerator, (BrokerMutationTreatment) treatment);
            }
            else if (treatment instanceof FixedServerParametersTreatment) {
                serializeFixedServerParametersTreatment(jsonGenerator, (FixedServerParametersTreatment) treatment);
            }
            else {
                throw new IOException(
                    String.format("no suitable serializer available for treatment type '%s'", treatment.getClass()));
            }
            jsonGenerator.writeEndObject();
        }
    }

    private void serializeBrokerMutationTreatment(JsonGenerator jsonGenerator, BrokerMutationTreatment treatment) throws IOException {
        jsonGenerator.writeStringField("type", "Change broker set");
        jsonGenerator.writeObjectField("mutation", new Object() {
            @Getter String action = treatment.getAction().toString();
            @Getter Broker broker = treatment.getBroker();
        });
    }

    private void serializeFixedServerParametersTreatment(JsonGenerator jsonGenerator, FixedServerParametersTreatment treatment) throws IOException {
        jsonGenerator.writeStringField("type", "Set server parameter");
        jsonGenerator.writeObjectField("mutation", new Object() {
            @Getter
            List<Parameter> parameters = parseParameters(treatment.getParameters());
        });
    }

    private List<Parameter> parseParameters(Map<String, String> parameters) {
        return parameters.entrySet().stream()
            .map((entry) -> new ParameterImpl(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

}
