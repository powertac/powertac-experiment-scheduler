package org.powertac.rachma.exec;

public interface TaskDTOMapper {

    <T extends PersistentTask> PersistentTaskDTO toDTO(T task);

}
