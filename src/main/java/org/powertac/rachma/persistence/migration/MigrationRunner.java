package org.powertac.rachma.persistence.migration;

public interface MigrationRunner {

    void registerMigration(Migration migration);
    void runMigrations();

}
