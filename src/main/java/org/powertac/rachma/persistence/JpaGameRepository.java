package org.powertac.rachma.persistence;

import org.powertac.rachma.game.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface JpaGameRepository extends CrudRepository<Game, String> {

    @Query(
        value = "SELECT game.* FROM game INNER JOIN game_run ON game.id = game_run.game_id WHERE game_run.phase = 1 ORDER BY game.created_at LIMIT 1",
        nativeQuery = true)
    Game findFirstQueued();

}
