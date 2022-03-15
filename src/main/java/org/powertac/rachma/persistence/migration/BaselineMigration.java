package org.powertac.rachma.persistence.migration;

import org.powertac.rachma.job.MongoJobRepository;
import org.springframework.beans.factory.annotation.Value;

public class BaselineMigration implements Migration {

    @Value("${persistence.migration.baseline.enabled}")
    private Boolean baselineMigrationEnabled;

    private final MongoJobRepository jobRepository;

    public BaselineMigration(MongoJobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public String getName() {
        return "baseline";
    }

    @Override
    public void run() throws MigrationException {
        /*
         * 1. load games from old ES
         * 2. create baseline
         * 3. match config to games
         * 4. persist changes
         * 5. copy games files
         */
    }

    @Override
    public void rollback() throws MigrationException {}

    @Override
    public boolean shouldRun() {
        return baselineMigrationEnabled;
    }

}
