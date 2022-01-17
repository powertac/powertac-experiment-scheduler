package org.powertac.rachma.baseline;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

interface BaselineCrudRepository extends CrudRepository<Baseline, String> {

    @Query(value = "SELECT baseline_id FROM baseline_games WHERE game_id = ?1 LIMIT 1", nativeQuery = true)
    String getBaselineIdByGameId(String gameId);

}
