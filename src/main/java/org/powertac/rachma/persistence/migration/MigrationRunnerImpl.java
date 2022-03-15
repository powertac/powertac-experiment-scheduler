package org.powertac.rachma.persistence.migration;

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
            if (shouldRun(migration)) {
                runMigration(migration);
            }
        }
    }

    private boolean shouldRun(Migration migration) {
        return !migrationStatusRepository.existsAllByNameAndSuccessTrue(migration.getName())
            && migration.shouldRun();
    }

    private void runMigration(Migration migration) {
        MigrationStatus status = MigrationStatus.start(migration);
        try {
            logger.info(String.format("starting migration '%s'", migration.getName()));
            migration.run();
            status.completeNow();
            logger.info(String.format("successfully finished migration '%s'", migration.getName()));
        } catch (MigrationException e) {
            logger.error(String.format("migration '%s' failed", migration.getName()), e);
            try {
                migration.rollback();
                status.failNow();
            } catch (MigrationException r) {
                logger.error(String.format("migration rollback for '%s' failed; manual intervention is required", migration.getName()), e);
            }
        } finally {
            migrationStatusRepository.save(status);
        }
    }

}
