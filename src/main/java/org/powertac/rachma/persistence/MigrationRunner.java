package org.powertac.rachma.persistence;

public interface MigrationRunner {

    void registerMigration(Migration migration);
    void runMigrations();

}
