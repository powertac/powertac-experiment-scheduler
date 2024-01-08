package org.powertac.orchestrator.api.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.orchestrator.api.view.ExportOptionsView;
import org.powertac.orchestrator.api.view.TreatmentSpecView;
import org.powertac.orchestrator.baseline.Baseline;
import org.powertac.orchestrator.baseline.BaselineRepository;
import org.powertac.orchestrator.file.GameFileExporter;
import org.powertac.orchestrator.game.GameRepository;
import org.powertac.orchestrator.treatment.Treatment;
import org.powertac.orchestrator.treatment.TreatmentFactory;
import org.powertac.orchestrator.treatment.TreatmentRepository;
import org.powertac.orchestrator.treatment.TreatmentSpec;
import org.powertac.orchestrator.util.ID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/treatments")
public class TreatmentController {

    // TODO : move to file exporter
    @Value("${directory.host.export}")
    private String exportBasePath;

    private final BaselineRepository baselines;
    private final TreatmentFactory treatmentFactory;
    private final TreatmentRepository treatmentRepository;
    private final GameRepository gameRepository;
    private final GameFileExporter fileExporter;
    private final Logger logger;

    public TreatmentController(BaselineRepository baselines, TreatmentFactory treatmentFactory,
                               TreatmentRepository treatmentRepository, GameRepository gameRepository, GameFileExporter fileExporter) {
        this.baselines = baselines;
        this.treatmentFactory = treatmentFactory;
        this.treatmentRepository = treatmentRepository;
        this.gameRepository = gameRepository;
        this.fileExporter = fileExporter;
        logger = LogManager.getLogger(TreatmentController.class);
    }

    @PostMapping("/")
    @Deprecated
    public ResponseEntity<Treatment> create(@RequestBody TreatmentSpecView view) {
        try {
            Optional<Baseline> baseline = baselines.findById(view.getBaselineId());
            if (baseline.isPresent()) {
                // TODO : add baseline validation (all games & files present, etc.)
                if (null == view.getModifier().getId()) {
                    view.getModifier().setId(ID.gen());
                }
                // TODO : add treatment validation (e.g. if the changed parameter is set in the baseline, etc.)
                Treatment treatment = treatmentFactory.createFrom(new TreatmentSpec(
                    view.getName(),
                    baseline.get(),
                    view.getModifier()));
                treatmentRepository.save(treatment);
                return ResponseEntity.ok(treatment);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error(e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/")
    public ResponseEntity<Iterable<Treatment>> getTreatments() {
        try {
            return ResponseEntity.ok(treatmentRepository.findAll());
        } catch (Exception e) {
            logger.error(e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable String id) {
        try {
            Optional<Treatment> treatment = treatmentRepository.findById(id);
            if (treatment.isPresent()) {
                treatment.get().getGames()
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
        Optional<Treatment> treatment = treatmentRepository.findById(id);
        if (treatment.isPresent()) {
            try {
                fileExporter.exportGames(treatment.get().getGames(), export.getTarget(), export.getHostUri());
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
