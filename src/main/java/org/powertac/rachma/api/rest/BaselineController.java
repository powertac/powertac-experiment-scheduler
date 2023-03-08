package org.powertac.rachma.api.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.baseline.NewBaselineDTO;
import org.powertac.rachma.api.view.ExportOptionsView;
import org.powertac.rachma.baseline.*;
import org.powertac.rachma.file.GameFileExporter;
import org.powertac.rachma.file.GameGroupManifestBuilder;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRepository;
import org.powertac.rachma.validation.exception.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

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
    private final GameFileExporter fileExporter;
    private final GameGroupManifestBuilder manifestBuilder;
    private final Logger logger;

    public BaselineController(BaselineFactory factory, BaselineGameFactory gameFactory, BaselineRepository baselineRepository, GameRepository gameRepository, GameFileExporter fileExporter, GameGroupManifestBuilder manifestBuilder) {
        this.factory = factory;
        this.gameFactory = gameFactory;
        this.baselineRepository = baselineRepository;
        this.gameRepository = gameRepository;
        this.fileExporter = fileExporter;
        this.manifestBuilder = manifestBuilder;
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

    @PostMapping("/{id}/export")
    public ResponseEntity<String> cancel(@PathVariable String id, @RequestBody ExportOptionsView export) {
        Optional<Baseline> baseline = baselineRepository.findById(id);
        if (baseline.isPresent()) {
            try {
                fileExporter.exportGames(baseline.get().getGames(), export.getTarget(), export.getHostUri());
                Path hostExportPath = Paths.get(exportBasePath,export.getTarget());
                return ResponseEntity.ok(hostExportPath.toString());
            } catch (IOException e) {
                logger.error(e);
                return ResponseEntity.status(500).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
