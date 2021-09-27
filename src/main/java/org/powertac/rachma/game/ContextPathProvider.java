package org.powertac.rachma.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.powertac.rachma.file.PathContext;
import org.powertac.rachma.file.PathContextType;
import org.powertac.rachma.persistence.Migration;
import org.powertac.rachma.persistence.MigrationPathProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

@AllArgsConstructor
public class ContextPathProvider {


    private final PathContext context;

    public PathContextType getContextType() {
        return context.getType();
    }

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

    public Path migrationsDir() {
        return Paths.get(context.getRoot(), "migrations");
    }

    public MigrationPathProvider migration(Migration migration) {
        return new MigrationPathProvider(this, migration);
    }

}
