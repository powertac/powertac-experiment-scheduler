package org.powertac.rachma.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DeserializationHelper {

    @SuppressWarnings("unchecked")
    public static <T> T defaultDeserialize(JsonNode node, Class<T> clazz, DeserializationContext context) throws IOException {
        JavaType type = context.getTypeFactory().constructType(clazz);
        JsonDeserializer<?> deserializer = context.findNonContextualValueDeserializer(type);
        JsonParser nodeParser = node.traverse(context.getParser().getCodec());
        nodeParser.nextToken();
        return (T) deserializer.deserialize(nodeParser, context);
    }

    public static <T> Set<T> deserializeSet(JsonNode arrayNode, Class<T> targetClass, DeserializationContext context) throws IOException {
        if (arrayNode.isArray()) {
            Iterator<JsonNode> iterator = arrayNode.iterator();
            Set<T> set = new HashSet<>();
            while (iterator.hasNext()) {
                JsonNode entryNode = iterator.next();
                set.add(defaultDeserialize(entryNode, targetClass, context));
            }
            return set;
        }
        return null;
    }

}
