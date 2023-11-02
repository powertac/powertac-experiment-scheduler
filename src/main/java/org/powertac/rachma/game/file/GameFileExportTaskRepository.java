package org.powertac.rachma.game.file;

import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.treatment.Treatment;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface GameFileExportTaskRepository extends CrudRepository<GameFileExportTask, String> {

    Collection<GameFileExportTask> findAllByBaseline(Baseline baseline);
    Collection<GameFileExportTask> findAllByTreatment(Treatment treatment);

}
