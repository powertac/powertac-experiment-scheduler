package org.powertac.orchestrator.game.file;

import org.powertac.orchestrator.baseline.Baseline;
import org.powertac.orchestrator.treatment.Treatment;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface GameFileExportTaskRepository extends CrudRepository<GameFileExportTask, String> {

    Collection<GameFileExportTask> findAllByBaseline(Baseline baseline);
    Collection<GameFileExportTask> findAllByTreatment(Treatment treatment);

}
