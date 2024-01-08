package org.powertac.orchestrator.game;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.broker.BrokerSet;
import org.powertac.orchestrator.util.DeserializationHelper;
import org.powertac.orchestrator.util.ID;
import org.powertac.orchestrator.weather.WeatherConfiguration;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameConfigDeserializer extends StdNodeBasedDeserializer<GameConfig> {

    public GameConfigDeserializer() {
        super(GameConfig.class);
    }

    @Override
    public GameConfig convert(JsonNode root, DeserializationContext context) throws IOException {
        return GameConfig.builder()
            .id(ID.gen())
            .brokers(parseBrokerSet(root.get("brokers"), context))
            .parameters(parseParameters(root.get("parameters")))
            .weather(DeserializationHelper.defaultDeserialize(root.get("weather"), WeatherConfiguration.class, context))
            .createdAt(Instant.now())
            .build();
    }

    private BrokerSet parseBrokerSet(JsonNode brokersNode, DeserializationContext context) throws IOException {
        BrokerSet brokerSet = new BrokerSet();
        brokerSet.setId(ID.gen());
        Iterator<JsonNode> elements = brokersNode.elements();
        while (elements.hasNext()) {
            brokerSet.addBroker(
                DeserializationHelper.defaultDeserialize(
                    elements.next(), Broker.class, context));
        }
        return brokerSet;
    }

    private Map<String, String> parseParameters(JsonNode paramsNode) {
        Map<String, String> parameters = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = paramsNode.fields();
        while(fields.hasNext()) {
            Map.Entry<String, JsonNode> node = fields.next();
            parameters.put(node.getKey(), node.getValue().asText());
        }
        return parameters;
    }

}
