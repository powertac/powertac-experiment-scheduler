package org.powertac.rachma.powertac.broker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.powertac.rachma.broker.BrokerType;

import java.io.IOException;

@Deprecated
public class BrokerTypeSerializer extends StdSerializer<BrokerType> {

    public BrokerTypeSerializer() {
        super(BrokerType.class);
    }

    @Override
    public void serialize(BrokerType brokerType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (brokerType != null) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", brokerType.getName());
            jsonGenerator.writeStringField("image", brokerType.getImage());
            jsonGenerator.writeBooleanField("disabled", !brokerType.isEnabled());
            jsonGenerator.writeEndObject();
        }
    }

}
