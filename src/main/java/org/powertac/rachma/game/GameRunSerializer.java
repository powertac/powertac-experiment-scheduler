package org.powertac.rachma.game;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;

public class GameRunSerializer extends StdSerializer<GameRun> {

    public GameRunSerializer() {
        super(GameRun.class);
    }

    @Override
    public void serialize(GameRun run, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", run.getId());
        gen.writeStringField("game", run.getGame().getId());
        instantNumberOrNullField("start", run.getStart(), gen);
        instantNumberOrNullField("end", run.getEnd(), gen);
        gen.writeStringField("phase", run.getPhase().toString());
        gen.writeBooleanField("failed", run.hasFailed());
        gen.writeEndObject();
    }

    private void instantNumberOrNullField(String field, Instant instant, JsonGenerator gen) throws IOException {
        if (null != instant) {
            gen.writeNumberField(field, instant.toEpochMilli());
        } else {
            gen.writeNullField(field);
        }
    }

}
