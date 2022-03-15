package org.powertac.rachma.persistence.migration;

public interface Migration {

    String getName();
    void run() throws MigrationException;
    void rollback() throws MigrationException;
    boolean shouldRun();

}
