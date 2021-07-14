package org.powertac.rachma.file;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRepository;

import java.io.IOException;

public class FileDeserializer extends StdNodeBasedDeserializer<File> {

    private final GameRepository games;

    public FileDeserializer(GameRepository games) {
        super(File.class);
        this.games = games;
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
        Game game = games.findById(root.get("game").asText());
        return new File(null, role, game);
    }

}
