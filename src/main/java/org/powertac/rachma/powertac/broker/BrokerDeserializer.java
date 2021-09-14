package org.powertac.rachma.powertac.broker;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.powertac.broker.Broker;
import org.powertac.rachma.powertac.broker.BrokerImpl;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.util.DeserializationHelper;

import java.io.IOException;

@Deprecated
public class BrokerDeserializer extends StdNodeBasedDeserializer<Broker> {

    public BrokerDeserializer() {
        super(Broker.class);
    }

    @Override
    public Broker convert(JsonNode jsonNode, DeserializationContext context) throws IOException {
        return new BrokerImpl(getId(jsonNode), getName(jsonNode), getType(jsonNode, context));
    }

    private String getId(JsonNode node) {
        return node.get("id").asText();
    }

    private String getName(JsonNode node) {
        return node.get("name").asText();
    }

    private BrokerType getType(JsonNode node, DeserializationContext context) throws IOException {
        return DeserializationHelper.defaultDeserialize(node.get("type"), BrokerType.class, context);
    }

}
