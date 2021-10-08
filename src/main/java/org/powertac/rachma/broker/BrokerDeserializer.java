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
        return new Broker(
            root.has("id") ? root.get("id").asText() : null,
            deserializeName(root),
            deserializeVersion(root),
            deserializeImageTag(root),
            false);
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
        return "latest";
    }

    private String deserializeImageTag(JsonNode root) throws IOException {
        if (root.has("imageTag")) {
            return root.get("imageTag").asText();
        }
        throw new IOException("Missing required field 'imageTag'");
    }

}
