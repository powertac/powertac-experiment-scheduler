package org.powertac.rachma.instance;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.serialization.DeserializationHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InstanceDeserializer extends StdNodeBasedDeserializer<Instance> {

    public InstanceDeserializer() {
        super(Instance.class);
    }

    @Override
    public Instance convert(JsonNode root, DeserializationContext context) throws IOException {
        Set<Broker> brokers = parseBrokers(root, context);
        ServerParameters serverParameters = parseServerParameters(root);
        return new InstanceImpl(brokers, serverParameters);
    }

    private ServerParameters parseServerParameters(JsonNode root) {
        Map<String, String> serverParameters = new HashMap<>();
        if (root.has("serverParameters")) {
            JsonNode serverParamsNode = root.get("serverParameters");
            serverParamsNode.forEach((node) -> {
                serverParameters.put(node.get("key").asText(), node.get("value").asText());
            });
        }
        return new TransientServerParameters(serverParameters);
    }

    private Set<Broker> parseBrokers(JsonNode root, DeserializationContext context) throws IOException {
        if (root.has("brokers")) {
            return DeserializationHelper.deserializeSet(
                root.get("brokers"),
                Broker.class,
                context);
        }
        return new HashSet<>();
    }

}
