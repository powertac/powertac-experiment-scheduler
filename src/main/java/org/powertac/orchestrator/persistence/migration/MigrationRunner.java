package org.powertac.orchestrator.persistence.migration;

public interface MigrationRunner {

    void registerMigration(Migration migration);
    void runMigration(String name) throws MigrationException;
    void forceMigration(String name) throws MigrationException;

}
