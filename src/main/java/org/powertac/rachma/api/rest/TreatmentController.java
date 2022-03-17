package org.powertac.rachma.api.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.api.view.TreatmentSpecView;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.baseline.BaselineRepository;
import org.powertac.rachma.treatment.Treatment;
import org.powertac.rachma.treatment.TreatmentFactory;
import org.powertac.rachma.treatment.TreatmentRepository;
import org.powertac.rachma.treatment.TreatmentSpec;
import org.powertac.rachma.util.ID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/treatments")
public class TreatmentController {

    private final BaselineRepository baselines;
    private final TreatmentFactory treatmentFactory;
    private final TreatmentRepository treatmentRepository;
    private final Logger logger;

    public TreatmentController(BaselineRepository baselines, TreatmentFactory treatmentFactory,
                               TreatmentRepository treatmentRepository) {
        this.baselines = baselines;
        this.treatmentFactory = treatmentFactory;
        this.treatmentRepository = treatmentRepository;
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

}
