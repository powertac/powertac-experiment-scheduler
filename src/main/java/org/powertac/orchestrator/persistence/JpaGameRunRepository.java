package org.powertac.orchestrator.persistence;

import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;
import org.powertac.orchestrator.game.GameRunPhase;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface JpaGameRunRepository extends CrudRepository<GameRun, String> {

    Collection<GameRun> findAllByGameAndPhaseBetween(Game game, GameRunPhase start, GameRunPhase end);
    Collection<GameRun> findAllByFailed(boolean failed);
    boolean existsByGameAndPhaseAndFailed(Game game, GameRunPhase phase, boolean failed);
    boolean existsByGameAndPhaseNot(Game game, GameRunPhase phase);

}
