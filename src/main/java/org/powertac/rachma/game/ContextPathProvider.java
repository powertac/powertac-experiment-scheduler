package org.powertac.rachma.game;

import lombok.AllArgsConstructor;
import org.powertac.rachma.file.PathContext;
import org.powertac.rachma.file.PathContextType;

import java.nio.file.Path;
import java.nio.file.Paths;

@AllArgsConstructor
public class ContextPathProvider {

    private final PathContext context;

    public ContextPathProvider(PathContextType type, String root) {
        this.context = new PathContext(type, root);
    }

    public GamePathProvider game(Game game) {
        return new GamePathProvider(context, game);
    }

    public Path gamesDir() {
        return Paths.get(context.getRoot(), "games");
    }

    public Path brokersDir() {
        return Paths.get(context.getRoot(), "brokers");
    }

}
