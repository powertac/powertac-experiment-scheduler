package org.powertac.rachma.treatment;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.util.DeserializationHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModifierDeserializer extends StdNodeBasedDeserializer<Modifier> {

    public ModifierDeserializer() {
        super(Modifier.class);
    }

    @Override
    public Modifier convert(JsonNode root, DeserializationContext context) throws IOException {
        if (root.hasNonNull("type")) {
            String type = root.get("type").asText();
            switch (type) {
                case "replace-broker":
                    return parseReplaceBrokerModifier(root, context);
                case "parameter-set":
                    return parseParameterSetModifier(root);
                default:
                    throw new IOException(String.format("'%s' is not a valid modifier type", type));
            }
        } else {
            throw new IOException("missing required field 'type'");
        }
    }

    private ReplaceBrokerModifier parseReplaceBrokerModifier(JsonNode root, DeserializationContext context) throws IOException {
        return new ReplaceBrokerModifier(
            null,
            root.get("name").asText(),
            DeserializationHelper.defaultDeserialize(root.get("original"), Broker.class, context),
            DeserializationHelper.defaultDeserialize(root.get("replacement"), Broker.class, context));
    }

    private ParameterSetModifier parseParameterSetModifier(JsonNode root) {
        return new ParameterSetModifier(
            null,
            root.get("name").asText(),
            deserializeParameterMap(root.get("parameters")));
    }

    private Map<String, String> deserializeParameterMap(JsonNode mapNode) {
        Map<String, String> parameters = new HashMap<>();
        mapNode.fields()
            .forEachRemaining(
                (node) -> parameters.put(node.getKey(), node.getValue().asText()));
        return parameters;
    }

}
