package org.powertac.rachma.treatment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ModifierSerializer extends StdSerializer<Modifier> {

    public ModifierSerializer() {
        super(Modifier.class);
    }

    @Override
    public void serialize(Modifier modifier, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (null == modifier) {
            throw new IOException("modifier is null");
        } else if (modifier instanceof ReplaceBrokerModifier) {
            serialize((ReplaceBrokerModifier) modifier, gen, provider);
        } else {
            throw new IOException(String.format("modifier of type %s is not serializable", modifier.getClass()));
        }
    }

    private void serialize(ReplaceBrokerModifier modifier, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "replace-broker");
        gen.writeStringField("id", modifier.getId());
        gen.writeStringField("name", modifier.getName());
        provider.defaultSerializeField("original", modifier.getOriginal(), gen);
        provider.defaultSerializeField("replacement", modifier.getOriginal(), gen);
        gen.writeEndObject();
    }

}
