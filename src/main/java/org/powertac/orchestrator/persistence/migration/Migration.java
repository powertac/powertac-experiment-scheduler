package org.powertac.orchestrator.persistence.migration;

public interface Migration {

    String getName();
    void run() throws MigrationException;
    void rollback() throws MigrationException;

}
