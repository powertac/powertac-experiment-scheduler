package org.powertac.orchestrator.exec;

public interface TaskDTOMapper {

    <C> PersistentTaskDTO<C> toDTO(Task task);

}
