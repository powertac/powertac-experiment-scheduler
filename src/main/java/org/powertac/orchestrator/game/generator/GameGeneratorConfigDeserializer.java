package org.powertac.orchestrator.game.generator;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.orchestrator.game.GameConfig;
import org.powertac.orchestrator.util.DeserializationHelper;
import org.powertac.orchestrator.util.ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameGeneratorConfigDeserializer extends StdNodeBasedDeserializer<GameGeneratorConfig> {

    public GameGeneratorConfigDeserializer() {
        super(GameGeneratorConfig.class);
    }

    @Override
    public GameGeneratorConfig convert(JsonNode root, DeserializationContext context) throws IOException {
        String type = root.get("type").asText();
        if(type.equals("game-multiplier")) {
            return parseMultiplierConfig(root, context);
        }
        throw new IOException(String.format("unknown game generator config type '%s'", type));
    }

    private MultiplierGameGeneratorConfig parseMultiplierConfig(JsonNode root, DeserializationContext context) throws IOException {
        MultiplierGameGeneratorConfig generatorConfig = new MultiplierGameGeneratorConfig();
        generatorConfig.setId(ID.gen());
        // TODO : We pick the first config of the list. Only 1 config supported at this time.
        List<GameConfig> configs = parseGameConfigs(root.get("games"), context);
        generatorConfig.setGameConfig(configs.get(0));
        generatorConfig.setMultiplier(root.get("multiplier").asInt());
        return generatorConfig;
    }

    private List<GameConfig> parseGameConfigs(JsonNode configsNode, DeserializationContext context) throws IOException {
        List<GameConfig> configs = new ArrayList<>();
        Iterator<JsonNode> elements = configsNode.elements();
        while (elements.hasNext()) {
            JsonNode node = elements.next();
            configs.add(DeserializationHelper.defaultDeserialize(node, GameConfig.class, context));
        }
        return configs;
    }

}
