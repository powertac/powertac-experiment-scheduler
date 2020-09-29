package org.powertac.rachma.treatment;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TreatmentDeserializer extends StdNodeBasedDeserializer<Treatment> {

    public TreatmentDeserializer() {
        super(Treatment.class);
    }

    @Override
    public Treatment convert(JsonNode root, DeserializationContext context) throws IOException {
        String type = root.get("type").asText();
        JsonNode mutationNode = root.get("mutation");
        switch (type) {
            case "Set server parameter":
                return deserializeFixedServerParameter(mutationNode);
            case "Change broker set":
                return deserializeBrokerMutation(mutationNode);
            default:
                throw new IOException(String.format("no deserializer available for type '%s'", type));
        }
    }

    private Treatment deserializeFixedServerParameter(JsonNode mutationNode) {
        JsonNode parametersNode = mutationNode.get("parameters");
        Map<String, String> parameters = new HashMap<>();
        parametersNode.forEach((paramNode) -> parameters.put(paramNode.get("key").asText(), paramNode.get("value").asText()));
        return new FixedServerParametersTreatment(parameters);
    }

    private Treatment deserializeBrokerMutation(JsonNode mutationNode) {
        return new BrokerMutationTreatment(
            parseAction(mutationNode.get("action").asText()),
            parseBroker(mutationNode.get("broker"))
        );
    }

    private BrokerMutationTreatment.Action parseAction(String actionReference) {
        switch (actionReference) {
            case "add":
                return BrokerMutationTreatment.Action.ADD;
            case "remove":
                return BrokerMutationTreatment.Action.REMOVE;
        }
        throw new IllegalArgumentException(String.format("'%s' is not a valid action reference", actionReference));
    }

    private Broker parseBroker(JsonNode brokerNode) {
        return new BrokerImpl(
            brokerNode.get("name").asText(),
            brokerNode.get("version").asText()
        );
    }

}
