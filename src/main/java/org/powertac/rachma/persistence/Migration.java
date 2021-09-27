package org.powertac.rachma.persistence;

public interface Migration {

    String getName();
    void run() throws MigrationException;
    void rollback() throws MigrationException;
    boolean shouldRun();

}
