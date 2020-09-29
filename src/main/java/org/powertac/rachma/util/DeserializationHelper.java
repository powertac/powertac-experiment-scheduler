package org.powertac.rachma.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class DeserializationHelper {

    @SuppressWarnings("unchecked")
    public static <T> T defaultDeserialize(JsonNode node, Class<T> clazz, DeserializationContext context) throws IOException {
        JavaType type = context.getTypeFactory().constructType(clazz);
        JsonDeserializer<?> deserializer = context.findNonContextualValueDeserializer(type);
        JsonParser nodeParser = node.traverse(context.getParser().getCodec());
        nodeParser.nextToken();
        return (T) deserializer.deserialize(nodeParser, context);
    }

    public static <T> List<T> parseAsList(JsonNode arrayNode, Function<JsonNode, T> entryParser) {
        if (arrayNode.isArray()) {
            Iterator<JsonNode> iterator = arrayNode.iterator();
            List<T> list = new ArrayList<>();
            while (iterator.hasNext()) {
                JsonNode entryNode = iterator.next();
                list.add(entryParser.apply(entryNode));
            }
            return list;
        }
        return null;
    }

    public static <T> Set<T> parseAsSet(JsonNode arrayNode, Function<JsonNode, T> entryParser) {
        if (arrayNode.isArray()) {
            Iterator<JsonNode> iterator = arrayNode.iterator();
            Set<T> set = new HashSet<>();
            while (iterator.hasNext()) {
                JsonNode entryNode = iterator.next();
                set.add(entryParser.apply(entryNode));
            }
            return set;
        }
        return null;
    }

    public static <T,S> Map<T,S> parseAsMap(JsonNode arrayNode, Function<JsonNode, Map<T,S>> entryParser) {
        if (arrayNode.isArray()) {
            Iterator<JsonNode> iterator = arrayNode.iterator();
            Map<T,S> map = new HashMap<>();
            while (iterator.hasNext()) {
                JsonNode entryNode = iterator.next();
                map.putAll(entryParser.apply(entryNode));
            }
            return map;
        }
        return null;
    }

}
