package org.powertac.rachma.persistence;

import org.powertac.rachma.file.PathContextType;
import org.powertac.rachma.game.ContextPathProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MigrationPathProvider {

    private final ContextPathProvider context;
    private final Migration migration;

    public MigrationPathProvider(ContextPathProvider context, Migration migration) {
        this.context = context;
        this.migration = migration;
    }

    public Path dir() {
        if (context.getContextType().equals(PathContextType.CONTAINER)) {
            return null;
        }
        return Paths.get(context.migrationsDir().toString(), migration.getName());
    }

}
