package org.powertac.orchestrator.file;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.orchestrator.game.Game;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;

@Component
public class FileDeserializer extends StdNodeBasedDeserializer<File> {

    public FileDeserializer() {
        super(File.class);
    }

    @Override
    public File convert(JsonNode root, DeserializationContext context) throws IOException {
        // For now only the game id and file role will identify the file; after file management is implemented in more
        // detail persistence-based deserialization must be added
        if (!root.has("role")) {
            throw new IOException("missing required node 'role'");
        }
        FileRole role = FileRole.valueOf(root.get("role").asText());
        if (!root.has("game")) {
            throw new IOException("missing required node 'game'");
        }
        Game game = new Game();
        game.setId(root.get("game").asText());
        return new File(null, role, game, "", new HashSet<>());
    }

}
