package org.powertac.rachma.powertac.broker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.powertac.rachma.powertac.broker.Broker;

import java.io.IOException;

@Deprecated
public class BrokerSerializer extends StdSerializer<Broker> {

    public BrokerSerializer() {
        super(Broker.class);
    }

    @Override
    public void serialize(Broker broker, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (broker != null) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", broker.getId());
            jsonGenerator.writeStringField("name", broker.getName());
            serializerProvider.defaultSerializeField("type", broker.getType(), jsonGenerator);
            jsonGenerator.writeEndObject();
        }
    }

}
