package org.powertac.rachma.treatment;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.util.DeserializationHelper;

import java.io.IOException;

public class ModifierDeserializer extends StdNodeBasedDeserializer<Modifier> {

    public ModifierDeserializer() {
        super(Modifier.class);
    }

    @Override
    public Modifier convert(JsonNode root, DeserializationContext context) throws IOException {
        if (root.hasNonNull("type")) {
            if ("replace-broker".equals(root.get("type").asText())) {
                return parseReplaceBrokerModifier(root, context);
            } else {
                throw new IOException("missing required field 'type'");
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

}
