package org.powertac.rachma.instance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;

import java.io.IOException;
import java.util.Map;

public class ServerParametersSerializer extends StdSerializer<ServerParameters> {

    public ServerParametersSerializer() {
        super(ServerParameters.class);
    }

    @Override
    public void serialize(ServerParameters parameters, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (Map.Entry<String, String> entry : parameters.getParameters().entrySet()) {
            jsonGenerator.writeObject(new Object() {
                @Getter String key = entry.getKey();
                @Getter String value = entry.getValue();
            });
        }
        jsonGenerator.writeEndArray();
    }
}
