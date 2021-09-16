package org.powertac.rachma.broker;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;

import java.io.IOException;

@Deprecated
public class BrokerTypeDeserializer extends StdNodeBasedDeserializer<BrokerType> {

    public BrokerTypeDeserializer() {
        super(BrokerType.class);
    }

    @Override
    public BrokerType convert(JsonNode node, DeserializationContext context) throws IOException {
        String name = node.get("name").asText();
        String imageReference = node.get("image").asText();
        boolean isDisabled = node.has("disabled") && node.get("disabled").asBoolean();
        return new BrokerTypeImpl(name, imageReference, !isDisabled);
    }

}
