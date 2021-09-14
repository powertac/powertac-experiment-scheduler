package org.powertac.rachma.game;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.file.File;
import org.powertac.rachma.serialization.DeserializationHelper;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class GameDeserializer extends StdNodeBasedDeserializer<Game> {

    public GameDeserializer() {
        super(Game.class);
    }

    @Override
    public Game convert(JsonNode root, DeserializationContext context) throws IOException {
        String id = root.has("id") ? root.get("id").asText() : null;
        String name = root.get("name").asText();
        Set<Broker> brokers = parseBrokers(root, context);
        Map<String, String> serverParameters = parseServerParameters(root);
        File bootstrap = root.has("bootstrap") ? parseFile(root.get("bootstrap"), context) : null;
        File seed = root.has("seed") ? parseFile(root.get("seed"), context) : null;
        boolean cancelled = root.has("cancelled") && root.get("cancelled").asBoolean();
        return new Game(id, name, brokers, serverParameters, bootstrap, seed, Instant.now(), new ArrayList<>(), cancelled);
    }

    private Map<String, String> parseServerParameters(JsonNode root) {
        Map<String, String> serverParameters = new HashMap<>();
        if (root.has("serverParameters")) {
            JsonNode serverParamsNode = root.get("serverParameters");
            serverParamsNode.forEach((node) -> {
                serverParameters.put(node.get("key").asText(), node.get("value").asText());
            });
        }
        return serverParameters;
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

    private File parseFile(JsonNode fileNode, DeserializationContext context) throws IOException {
        return DeserializationHelper.defaultDeserialize(fileNode, File.class, context);
    }

}
