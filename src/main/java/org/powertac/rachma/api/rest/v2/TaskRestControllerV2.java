package org.powertac.rachma.api.rest.v2;

import org.powertac.rachma.exec.PersistentTaskDTO;
import org.powertac.rachma.exec.PersistentTaskRepository;
import org.powertac.rachma.exec.TaskDTOMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/v2/tasks")
public class TaskRestControllerV2 {

    private final PersistentTaskRepository taskRepository;
    private final TaskDTOMapper mapper;

    public TaskRestControllerV2(PersistentTaskRepository taskRepository, TaskDTOMapper mapper) {
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    @GetMapping("/")
    public ResponseEntity<Collection<PersistentTaskDTO>> getTasks() {
        return ResponseEntity.ok(
            StreamSupport.stream(taskRepository.findAll().spliterator(), false)
                .map(mapper::toDTO)
                .collect(Collectors.toList()));
    }

}
