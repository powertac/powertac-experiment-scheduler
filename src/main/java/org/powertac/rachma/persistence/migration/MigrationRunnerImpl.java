package org.powertac.rachma.persistence.migration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MigrationRunnerImpl implements MigrationRunner {

    private final MigrationStatusRepository migrationStatusRepository;
    private final Map<String, Migration> migrations;
    private final Logger logger;

    public MigrationRunnerImpl(MigrationStatusRepository migrationStatusRepository) {
        this.migrationStatusRepository = migrationStatusRepository;
        this.migrations = new HashMap<>();
        this.logger = LogManager.getLogger(MigrationRunner.class);
    }

    @Override
    public void registerMigration(Migration migration) {
        migrations.put(migration.getName(), migration);
    }

    @Override
    public void runMigration(String name) throws MigrationException {
        if (migrations.containsKey(name)) {
            Migration migration = migrations.get(name);
            if (!isAlreadyComplete(migration)) {
                runMigration(migration);
            } else {
                logger.warn(String.format("migration '%s' has already been completed", name));
            }
        } else {
            throw new MigrationException(String.format("migration '%s' does not exist", name));
        }
    }

    @Override
    public void forceMigration(String name) throws MigrationException {
        if (migrations.containsKey(name)) {
            Migration migration = migrations.get(name);
            runMigration(migration);
        } else {
            throw new MigrationException(String.format("migration '%s' does not exist", name));
        }
    }

    private boolean isAlreadyComplete(Migration migration) {
        return migrationStatusRepository.existsByNameAndSuccessTrue(migration.getName());
    }

    private void runMigration(Migration migration) throws MigrationException {
        MigrationStatus status = MigrationStatus.start(migration);
        try {
            logger.info(String.format("starting migration '%s'", migration.getName()));
            migration.run();
            status.completeNow();
            logger.info(String.format("successfully finished migration '%s'", migration.getName()));
        } catch (MigrationException e) {
            logger.error(String.format("migration '%s' failed", migration.getName()), e);
            status.failNow();
            try {
                migration.rollback();
                throw e;
            } catch (MigrationException r) {
                logger.error(String.format(
                    "migration rollback for '%s' failed; manual intervention is required!!!", migration.getName()), e);
                throw r;
            }
        } finally {
            migrationStatusRepository.save(status);
        }
    }

}
