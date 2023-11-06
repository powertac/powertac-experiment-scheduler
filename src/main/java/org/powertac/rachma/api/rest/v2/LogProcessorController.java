package org.powertac.rachma.api.rest.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.exec.PersistentTaskDTO;
import org.powertac.rachma.exec.TaskDTOMapper;
import org.powertac.rachma.exec.TaskScheduler;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRepository;
import org.powertac.rachma.logprocessor.*;
import org.powertac.rachma.user.UserNotFoundException;
import org.powertac.rachma.user.UserProvider;
import org.powertac.rachma.util.ID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/log-processors")
public class LogProcessorController {

    private final UserProvider userProvider;
    private final LogProcessorProvider processorProvider;
    private final GameRepository gameRepository;
    private final TaskScheduler taskScheduler;
    private final LogProcessorTaskRepository taskRepository;
    private final TaskDTOMapper dtoMapper;
    private final Logger logger;

    public LogProcessorController(UserProvider userProvider, LogProcessorProvider processorProvider,
                                  GameRepository gameRepository, TaskScheduler taskScheduler,
                                  LogProcessorTaskRepository taskRepository, TaskDTOMapper dtoMapper) {
        this.userProvider = userProvider;
        this.processorProvider = processorProvider;
        this.gameRepository = gameRepository;
        this.taskScheduler = taskScheduler;
        this.taskRepository = taskRepository;
        this.dtoMapper = dtoMapper;
        logger = LogManager.getLogger(LogProcessorController.class);
    }

    @GetMapping("/available")
    public ResponseEntity<Set<LogProcessor>> getAvailableProcessors() {
        try {
            return ResponseEntity.ok(processorProvider.getAvailableProcessors());
        } catch (Exception e) {
            logger.error("unable to serve available log processors");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<PersistentTaskDTO<LogProcessorTaskConfig>> processLog(@RequestBody LogProcessorTaskConfig dto) {
        try {
            Game game = gameRepository.findById(dto.getGameId());
            if (game == null) {
                logger.error("unable to find game with id=" + dto.getGameId());
                return ResponseEntity.notFound().build();
            } else if (!dto.getProcessorNames().stream().allMatch(processorProvider::hasProcessor)) {
                logger.error("one or more processors do not exist; " + String.join(", ", dto.getProcessorNames()));
                return ResponseEntity.badRequest().build();
            } else {
                LogProcessorTask task = createTask(game, dto.getProcessorNames());
                taskScheduler.add(task);
                return ResponseEntity.ok(dtoMapper.toDTO(task));
            }
        } catch (UserNotFoundException e) {
            logger.error("unable to determine current user", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<Collection<PersistentTaskDTO<LogProcessorTaskConfig>>> getForGame(@PathVariable String id) {
        try {
            Game game = gameRepository.findById(id);
            if (null != game) {
                return ResponseEntity.ok(taskRepository.findAllByGame(game).stream()
                    .map(dtoMapper::<LogProcessorTaskConfig>toDTO)
                    .collect(Collectors.toSet()));
            } else {
                logger.error("no game found with id=" + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("unable to serve log processor tasks for game with id=" + id);
            return ResponseEntity.internalServerError().build();
        }
    }

    private LogProcessorTask createTask(Game game, Set<String> processorNames) throws UserNotFoundException {
        return LogProcessorTask.builder()
            .id(ID.gen())
            .creator(userProvider.getCurrentUser())
            .createdAt(Instant.now())
            .game(game)
            .processorIds(processorNames)
            .build();
    }

}