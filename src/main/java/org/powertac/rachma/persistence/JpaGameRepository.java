package org.powertac.rachma.persistence;

import org.powertac.rachma.game.Game;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaGameRepository extends CrudRepository<Game, String> {

    @Query(
        value = "SELECT game.* FROM game LEFT OUTER JOIN game_run_stats grs on game.id = grs.game_id where (grs.run_count IS NULL OR (grs.run_count < 5 AND grs.success != 1)) AND NOT game.cancelled order by created_at LIMIT 1",
        nativeQuery = true)
    Game findFirstQueued();

    Optional<Game> findOneByName(String name);

}
