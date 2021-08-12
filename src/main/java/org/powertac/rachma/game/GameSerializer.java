package org.powertac.rachma.game;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.powertac.rachma.broker.Broker;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class GameSerializer extends StdSerializer<Game> {

    public GameSerializer() {
        super(Game.class);
    }

    @Override
    public void serialize(Game game, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (null != game) {
            gen.writeStartObject();
            gen.writeStringField("id", game.getId());
            gen.writeStringField("name", game.getName());
            writeBrokersField(game.getBrokers(), gen, provider);
            gen.writeObjectField("bootstrap", game.getBootstrap());
            gen.writeObjectField("seed", game.getSeed());
            if (null != game.getRuns()) {
                writeRunsField(game.getRuns(), gen, provider);
            }
            gen.writeEndObject();
        }
    }

    private void writeBrokersField(Set<Broker> brokers, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeArrayFieldStart("brokers");
        for (Broker broker: brokers) {
            provider.defaultSerializeValue(broker, gen);
        }
        gen.writeEndArray();
    }

    private void writeRunsField(List<GameRun> runs, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeArrayFieldStart("runs");
        for (GameRun run : runs) {
            provider.defaultSerializeValue(run, gen);
        }
        gen.writeEndArray();
    }

}
