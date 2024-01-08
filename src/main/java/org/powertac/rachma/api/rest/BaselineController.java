package org.powertac.rachma.api.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.baseline.NewBaselineDTO;
import org.powertac.rachma.baseline.*;
import org.powertac.rachma.exec.PersistentTaskDTO;
import org.powertac.rachma.exec.TaskDTOMapper;
import org.powertac.rachma.exec.TaskScheduler;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRepository;
import org.powertac.rachma.game.file.GameFileExportTask;
import org.powertac.rachma.game.file.GameFileExportTaskConfig;
import org.powertac.rachma.game.file.GameFileExportTaskRepository;
import org.powertac.rachma.game.file.NewGameFileExportTaskDTO;
import org.powertac.rachma.user.exception.UserNotFoundException;
import org.powertac.rachma.user.UserProvider;
import org.powertac.rachma.util.ID;
import org.powertac.rachma.validation.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/baselines")
public class BaselineController {

    // TODO : move to file exporter
    @Value("${directory.host.export}")
    private String exportBasePath;

    private final BaselineFactory factory;
    private final BaselineGameFactory gameFactory;
    private final BaselineRepository baselineRepository;
    private final GameRepository gameRepository;
    private final UserProvider userProvider;
    private final TaskDTOMapper taskDTOMapper;
    private final TaskScheduler taskScheduler;
    private final GameFileExportTaskRepository exportTaskRepository;
    private final Logger logger;

    public BaselineController(BaselineFactory factory, BaselineGameFactory gameFactory,
                              BaselineRepository baselineRepository, GameRepository gameRepository,
                              UserProvider userProvider, TaskDTOMapper taskDTOMapper, TaskScheduler taskScheduler,
                              GameFileExportTaskRepository exportTaskRepository) {
        this.factory = factory;
        this.gameFactory = gameFactory;
        this.baselineRepository = baselineRepository;
        this.gameRepository = gameRepository;
        this.userProvider = userProvider;
        this.taskDTOMapper = taskDTOMapper;
        this.taskScheduler = taskScheduler;
        this.exportTaskRepository = exportTaskRepository;
        this.logger = LogManager.getLogger(BaselineController.class);
    }

    @GetMapping("/")
    public ResponseEntity<Iterable<Baseline>> getBaselines() {
        return ResponseEntity.ok(baselineRepository.findAll());
    }

    @PostMapping("/")
    public ResponseEntity<?> createBaseline(@RequestBody BaselineSpec spec) {
        try {
            Baseline baseline = factory.createFromSpec(spec);
            baselineRepository.save(baseline);
            List<Game> games = gameFactory.createGames(baseline);
            baseline.setGames(games);
            baselineRepository.save(baseline);
            return ResponseEntity.ok(baseline);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("server error during baseline creation", e);
            return ResponseEntity.status(500).body("a server error occured; check the orchestrator logs for details");
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateBaseline(@RequestBody NewBaselineDTO view) {
        try {
            Baseline baseline = factory.generate(view.getName(), view.getGenerator());
            baselineRepository.save(baseline);
            return ResponseEntity.ok().body(baseline);
        } catch (ValidationException e) {
            logger.error(e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("server error during baseline creation", e);
            return ResponseEntity.status(500).body("a server error occured; check the orchestrator logs for details");
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        try {
            Optional<Baseline> baseline = baselineRepository.findById(id);
            if (baseline.isPresent()) {
                baseline.get().getGames()
                    .forEach(game -> {
                        game.setCancelled(true);
                        gameRepository.save(game);
                    });
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(404).build();
            }
        } catch (Exception e) {
            logger.error(e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{id}/exports")
    public ResponseEntity<PersistentTaskDTO<GameFileExportTaskConfig>> export(@PathVariable String id, @RequestBody NewGameFileExportTaskDTO options) {
        Optional<Baseline> baseline = baselineRepository.findById(id);
        if (baseline.isPresent()) {
            try {
                GameFileExportTask task = createExportTask(baseline.get(), options);
                taskScheduler.add(task);
                return ResponseEntity.ok(taskDTOMapper.toDTO(task));
            } catch (UserNotFoundException e) {
                logger.error("unable to create game file export task for baseline with id=" + id, e);
                return ResponseEntity.status(500).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/exports")
    public ResponseEntity<Collection<PersistentTaskDTO<GameFileExportTaskConfig>>> getExportTasks(@PathVariable String id) {
        try {
            Optional<Baseline> baseline = baselineRepository.findById(id);
            if (baseline.isPresent()) {
                return ResponseEntity.ok(
                    exportTaskRepository.findAllByBaseline(baseline.get()).stream()
                        .map(taskDTOMapper::<GameFileExportTaskConfig>toDTO)
                        .collect(Collectors.toList()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("unable to get tasks for baseline with id=" + id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    private GameFileExportTask createExportTask(Baseline baseline, NewGameFileExportTaskDTO dto) throws UserNotFoundException {
        return GameFileExportTask.builder()
                .id(ID.gen())
                .creator(userProvider.getCurrentUser())
                .createdAt(Instant.now())
                .baseline(baseline)
                .target(dto.getTarget())
                .baseUri(dto.getBaseUri())
                .build();
    }

}
