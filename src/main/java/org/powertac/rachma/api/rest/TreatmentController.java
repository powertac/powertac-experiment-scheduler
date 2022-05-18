package org.powertac.rachma.api.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.api.view.TreatmentSpecView;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.baseline.BaselineRepository;
import org.powertac.rachma.game.GameRepository;
import org.powertac.rachma.treatment.Treatment;
import org.powertac.rachma.treatment.TreatmentFactory;
import org.powertac.rachma.treatment.TreatmentRepository;
import org.powertac.rachma.treatment.TreatmentSpec;
import org.powertac.rachma.util.ID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/treatments")
public class TreatmentController {

    private final BaselineRepository baselines;
    private final TreatmentFactory treatmentFactory;
    private final TreatmentRepository treatmentRepository;
    private final GameRepository gameRepository;
    private final Logger logger;

    public TreatmentController(BaselineRepository baselines, TreatmentFactory treatmentFactory,
                               TreatmentRepository treatmentRepository, GameRepository gameRepository) {
        this.baselines = baselines;
        this.treatmentFactory = treatmentFactory;
        this.treatmentRepository = treatmentRepository;
        this.gameRepository = gameRepository;
        logger = LogManager.getLogger(TreatmentController.class);
    }

    @PostMapping("/")
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

}
