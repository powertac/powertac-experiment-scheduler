package org.powertac.rachma.game;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.file.FileRole;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class GameSerializer extends StdSerializer<Game> {

    private final GameFileManager fileManager;

    public GameSerializer(GameFileManager fileManager) {
        super(Game.class);
        this.fileManager = fileManager;
    }

    @Override
    public void serialize(Game game, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("id", game.getId());
        gen.writeStringField("name", game.getName());
        gen.writeNumberField("createdAt", game.getCreatedAt().toEpochMilli());
        provider.defaultSerializeField("bootstrap", game.getBootstrap(), gen);
        provider.defaultSerializeField("seed", game.getSeed(), gen);
        writeBrokersField(game.getBrokers(), gen, provider);
        writeServerParametersField(game.getServerParameters(), gen);
        writeRunField(game.getRuns(), gen, provider);
        writeFilesField(fileManager.getFiles(game), gen);
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

    private void writeFilesField(Map<FileRole, String> files, JsonGenerator gen) throws IOException {
        gen.writeObjectFieldStart("files");
        for (Map.Entry<FileRole, String> file : files.entrySet()) {
            gen.writeStringField(file.getKey().toString(), file.getValue());
        }
        gen.writeEndObject();
    }

}
