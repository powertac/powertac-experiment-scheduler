package org.powertac.orchestrator.game;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.paths.PathProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GameSerializer extends StdSerializer<Game> {

    private final PathProvider paths;

    public GameSerializer(PathProvider paths) {
        super(Game.class);
        this.paths = paths;
    }

    @Override
    public void serialize(Game game, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", game.getId());
        gen.writeStringField("name", game.getName());
        gen.writeNumberField("createdAt", game.getCreatedAt().toEpochMilli());
        gen.writeBooleanField("cancelled", game.isCancelled());
        gen.writeBooleanField("isValidTemplate", isValidTemplate(game));
        provider.defaultSerializeField("bootstrap", game.getBootstrap(), gen);
        provider.defaultSerializeField("seed", game.getSeed(), gen);
        writeBrokersField(game.getBrokers(), gen, provider);
        writeServerParametersField(game.getServerParameters(), gen);
        writeRunField(game.getRuns(), gen, provider);
        if (null != game.getBaseline()) {
            gen.writeStringField("baselineId", game.getBaseline().getId());
        }
        if (null != game.getTreatment()) {
            gen.writeStringField("treatmentId", game.getTreatment().getId());
        }
        if (null != game.getBase()) {
            gen.writeStringField("baseId", game.getBase().getId());
        }
        provider.defaultSerializeField("weather", game.getWeatherConfiguration(), gen);
        gen.writeEndObject();
    }

    private void writeBrokersField(Set<Broker> brokers, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeArrayFieldStart("brokers");
        for (Broker broker: brokers) {
            provider.defaultSerializeValue(broker, gen);
        }
        gen.writeEndArray();
    }

    private void writeServerParametersField(Map<String, String> parameters, JsonGenerator gen) throws IOException {
        gen.writeObjectFieldStart("serverParameters");
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            gen.writeStringField(parameter.getKey(), parameter.getValue());
        }
        gen.writeEndObject();
    }

    private void writeRunField(Collection<GameRun> runs, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeArrayFieldStart("runs");
        for (GameRun run : runs) {
            provider.defaultSerializeValue(run, gen);
        }
        gen.writeEndArray();
    }

    // TODO : there might be an architectually more sound place for this
    private boolean isValidTemplate(Game game) {
        GameRun run = game.getLatestSuccessfulRun();
        return run != null
            && Files.exists(paths.local().run(run).state())
            && Files.exists(paths.local().game(game).bootstrap());
    }

}
