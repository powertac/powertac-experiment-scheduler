package org.powertac.rachma.persistence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
public class GameSchemaViewSeeder implements SchemaViewSeeder {

    private final EntityManager entityManager;

    public GameSchemaViewSeeder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void seedViews() {
        createGameRunStatsView();
    }

    private void createGameRunStatsView() {
        String statement = "CREATE OR REPLACE VIEW game_run_stats AS SELECT game_run.game_id, COUNT(*) as run_count, MIN(game_run.failed) = 0 as success FROM game_run WHERE game_run.phase = 5 GROUP BY game_run.game_id;";
        entityManager.createNativeQuery(statement).executeUpdate();
    }

}
