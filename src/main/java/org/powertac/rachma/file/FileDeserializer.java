package org.powertac.rachma.file;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
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
        String gameId = root.get("game").asText();
        Game game = games.findById(gameId);
        if (null == game) {
            throw new IOException(String.format("game '%s' does not exist", gameId));
        }
        return new File(null, role, game);
    }

}
