package org.powertac.rachma.broker;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BrokerDeserializer extends StdNodeBasedDeserializer<Broker> {

    public BrokerDeserializer() {
        super(Broker.class);
    }

    @Override
    public Broker convert(JsonNode root, DeserializationContext context) throws IOException {
        return new BrokerImpl(
            deserializeName(root),
            deserializeVersion(root),
            deserializeConfig(root));
    }

    private String deserializeName(JsonNode root) throws IOException {
        if (root.has("name")) {
            return root.get("name").asText();
        }
        throw new IOException("Missing required field 'name'");
    }

    private String deserializeVersion(JsonNode root) {
        if (root.has("version")) {
            return root.get("version").asText();
        }
        // TODO : replace empty string with default value (which could be provided by a broker repo for example)
        return "latest";
    }

    private Map<String, String> deserializeConfig(JsonNode root) {
        if (!root.has("config")) {
            return new HashMap<>();
        }
        JsonNode configNode = root.get("config");
        Map<String, String> config = new HashMap<>();
        Iterator<String> keys = configNode.fieldNames();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = configNode.get(key).asText();
            config.put(key, value);
        }
        return config;
    }

}
