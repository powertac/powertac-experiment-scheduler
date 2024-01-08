package org.powertac.orchestrator.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;

@Deprecated
public class InstantToNumberSerializer extends StdSerializer<Instant> {

    public InstantToNumberSerializer() {
        super(Instant.class);
    }

    @Override
    public void serialize(Instant instant, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(instant.toEpochMilli());
    }

}
