package org.powertac.rachma.exec;

import org.powertac.rachma.exec.PersistentTaskDTO.PersistentTaskDTOBuilder;
import org.powertac.rachma.game.file.GameFileExportTask;
import org.powertac.rachma.game.file.GameFileExportTaskDTO;
import org.powertac.rachma.game.file.GameFileExportTaskDTO.GameFileExportTaskDTOBuilder;
import org.springframework.stereotype.Component;

@Component
public class TaskDTOMapperImpl implements TaskDTOMapper {

    @Override
    public <T extends PersistentTask> PersistentTaskDTO toDTO(T task) {
        if (task instanceof GameFileExportTask) {
            return toDTO((GameFileExportTask) task, baseBuilder(task, GameFileExportTaskDTO.builder()));
        } else {
            throw new RuntimeException("no mapper for task of type " + task.getClass());
        }
    }

    private GameFileExportTaskDTO toDTO(GameFileExportTask task, GameFileExportTaskDTOBuilder builder) {
        builder.type("game-file-export");
        return builder
                .baselineId(task.getBaseline() != null ? task.getBaseline().getId() : null)
                .treatmentId(task.getTreatment() != null ? task.getTreatment().getId() : null)
                .target(task.getTarget())
                .baseUri(task.getBaseUri())
                .build();
    }

    private <B extends PersistentTaskDTOBuilder> B baseBuilder(PersistentTask task, B builder) {
        return (B) builder
                .id(task.getId())
                .creatorId(task.getCreator().getId())
                .createdAt(task.getCreatedAt().toEpochMilli())
                .start(task.getStart() != null ? task.getStart().toEpochMilli() : null)
                .end(task.getEnd() != null ? task.getEnd().toEpochMilli() : null)
                .priority(task.getPriority())
                .failed(task.isFailed());
    }

}
