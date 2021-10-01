package org.powertac.rachma.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.file.PathContextType;
import org.powertac.rachma.game.ContextPathProvider;

import java.nio.file.Paths;

public class MigrationPathProviderTests {

    @Test
    void localMigrationPathTest() {
        ContextPathProvider context = new ContextPathProvider(PathContextType.LOCAL, "/path/to/local/");
        Migration migration = Mockito.mock(Migration.class);
        Mockito.when(migration.getName()).thenReturn("sample-migration");
        MigrationPathProvider provider = new MigrationPathProvider(context, migration);
        Assertions.assertEquals(Paths.get("/path/to/local/migrations/sample-migration"), provider.dir());
    }

    @Test
    void hostMigrationPathTest() {
        ContextPathProvider context = new ContextPathProvider(PathContextType.HOST, "/path/to/host/");
        Migration migration = Mockito.mock(Migration.class);
        Mockito.when(migration.getName()).thenReturn("host-sample-migration");
        MigrationPathProvider provider = new MigrationPathProvider(context, migration);
        Assertions.assertEquals(Paths.get("/path/to/host/migrations/host-sample-migration"), provider.dir());
    }

    @Test
    void containerMigrationPathTest() {
        ContextPathProvider context = new ContextPathProvider(PathContextType.CONTAINER, "/path/in/container/");
        Migration migration = Mockito.mock(Migration.class);
        Mockito.when(migration.getName()).thenReturn("not-in-container-migration");
        MigrationPathProvider provider = new MigrationPathProvider(context, migration);
        Assertions.assertNull(provider.dir());
    }

}
