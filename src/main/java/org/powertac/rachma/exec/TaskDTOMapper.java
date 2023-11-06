package org.powertac.rachma.exec;

public interface TaskDTOMapper {

    <C> PersistentTaskDTO<C> toDTO(Task task);

}
