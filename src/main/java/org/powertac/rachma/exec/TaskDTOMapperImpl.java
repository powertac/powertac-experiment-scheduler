package org.powertac.rachma.exec;

import org.powertac.rachma.analysis.AnalyzerTask;
import org.powertac.rachma.analysis.AnalyzerTaskConfig;
import org.powertac.rachma.game.file.GameFileExportTask;
import org.powertac.rachma.game.file.GameFileExportTaskConfig;
import org.powertac.rachma.logprocessor.LogProcessorTask;
import org.powertac.rachma.logprocessor.LogProcessorTaskConfig;
import org.springframework.stereotype.Component;

@Component
public class TaskDTOMapperImpl implements TaskDTOMapper {

    @Override
    @SuppressWarnings("unchecked")
    public <C> PersistentTaskDTO<C> toDTO(Task task) {
        PersistentTaskDTO.PersistentTaskDTOBuilder builder = PersistentTaskDTO.builder()
            .id(task.getId())
            .creatorId(task.getCreator().getId())
            .createdAt(task.getCreatedAt().toEpochMilli())
            .start(task.getStart() != null ? task.getStart().toEpochMilli() : null)
            .end(task.getEnd() != null ? task.getEnd().toEpochMilli() : null)
            .priority(task.getPriority())
            .failed(task.hasFailed());
        if (task instanceof GameFileExportTask) {
            builder.type("game-file-export");
            builder.config(buildExportTaskConfig((GameFileExportTask) task));
        } else if (task instanceof LogProcessorTask) {
            builder.type("log-processor");
            builder.config(buildLogProcessorTaskConfig((LogProcessorTask) task));
        } else if (task instanceof AnalyzerTask) {
            builder.type("analyzer");
            builder.config(buildAnalyzerTaskConfig((AnalyzerTask) task));
        }
        return (PersistentTaskDTO<C>) builder.build();
    }

    private GameFileExportTaskConfig buildExportTaskConfig(GameFileExportTask task) {
        return GameFileExportTaskConfig.builder()
            .baselineId(task.getBaseline() != null ? task.getBaseline().getId() : null)
            .treatmentId(task.getTreatment() != null ? task.getTreatment().getId() : null)
            .target(task.getTarget())
            .baseUri(task.getBaseUri())
            .build();
    }

    private LogProcessorTaskConfig buildLogProcessorTaskConfig(LogProcessorTask task) {
        return LogProcessorTaskConfig.builder()
            .gameId(task.getGame().getId())
            .processorNames(task.getProcessorIds())
            .build();
    }

    private AnalyzerTaskConfig buildAnalyzerTaskConfig(AnalyzerTask task) {
        return AnalyzerTaskConfig.builder()
            .gameId(task.getGame() != null ? task.getGame().getId() : null)
            .baselineId(task.getBaseline() != null ? task.getBaseline().getId() : null)
            .treatmentId(task.getTreatment() != null ? task.getTreatment().getId() : null)
            .build();
    }

}
