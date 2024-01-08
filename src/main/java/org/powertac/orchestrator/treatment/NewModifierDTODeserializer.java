package org.powertac.orchestrator.treatment;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.orchestrator.util.DeserializationHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewModifierDTODeserializer extends StdNodeBasedDeserializer<NewModifierDTO> {

    public NewModifierDTODeserializer() {
        super(NewModifierDTO.class);
    }

    @Override
    public NewModifierDTO convert(JsonNode root, DeserializationContext context) throws IOException {
        ModifierType type = DeserializationHelper.defaultDeserialize(root.get("type"), ModifierType.class, context);
        ModifierConfigDTO config = type.equals(ModifierType.REPLACE_BROKER)
            ? deserializeReplaceBrokerConfig(root.get("config"))
            : deserializeParameterSetConfig(root.get("config"));
        return new NewModifierDTO(type, config);
    }

    private ModifierConfigDTO deserializeReplaceBrokerConfig(JsonNode node) {
        Map<String, String> replacements = new HashMap<>();
        node.get("brokerMapping")
            .fields()
            .forEachRemaining(entry -> replacements.put(entry.getKey(), entry.getValue().asText()));
        return new ReplaceBrokerModifierConfigDTO(replacements);
    }

    private ModifierConfigDTO deserializeParameterSetConfig(JsonNode node) {
        Map<String, String> parameters = new HashMap<>();
        node.get("parameters")
            .fields()
            .forEachRemaining(entry -> parameters.put(entry.getKey(), entry.getValue().asText()));
        return new ParameterSetModifierConfigDTO(parameters);
    }

}
