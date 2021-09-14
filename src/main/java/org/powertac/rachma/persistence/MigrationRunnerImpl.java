package org.powertac.rachma.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MigrationRunnerImpl implements MigrationRunner {

    private final MigrationStatusRepository migrationStatusRepository;
    private final List<Migration> migrations;
    private final Logger logger;

    public MigrationRunnerImpl(MigrationStatusRepository migrationStatusRepository) {
        this.migrationStatusRepository = migrationStatusRepository;
        this.migrations = new ArrayList<>();
        this.logger = LogManager.getLogger(MigrationRunner.class);
    }

    @Override
    public void registerMigration(Migration migration) {
        migrations.add(migration);
    }

    @Override
    public void runMigrations() {
        for(Migration migration : migrations) {
            if (!migrationStatusRepository.existsAllByNameAndSuccessTrue(migration.getName())) {
                logger.info(String.format("starting migration '%s'", migration.getName()));
                runMigration(migration);
            }
        }
    }

    private void runMigration(Migration migration) {
        MigrationStatus status = MigrationStatus.start(migration);
        try {
            migration.rollback();
            // TODO : this is just one time thing
            // migration.run();
            // status.completeNow();
        } catch (MigrationException e) {
            try {
                migration.rollback();
                status.failNow();
                logger.error(String.format("migration '%s' failed", migration.getName()), e);
            } catch (MigrationException r) {
                logger.error(String.format("migration rollback for '%s' failed; manual intervention required", migration.getName()), e);
            }
        } finally {
            migrationStatusRepository.save(status);
        }
    }

}
