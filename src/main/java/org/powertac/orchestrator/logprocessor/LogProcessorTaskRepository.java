package org.powertac.orchestrator.logprocessor;

import org.powertac.orchestrator.game.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface LogProcessorTaskRepository extends CrudRepository<LogProcessorTask, String> {

    Collection<LogProcessorTask> findAllByGame(Game game);

}
