package org.powertac.rachma.api.rest.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.baseline.Baseline;
import org.powertac.rachma.baseline.BaselineRepository;
import org.powertac.rachma.paths.PathProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/paths")
public class PathRestController {

    private final BaselineRepository baselineRepository;
    private final PathProvider paths;
    private final Logger logger;

    public PathRestController(BaselineRepository baselineRepository, PathProvider paths) {
        this.baselineRepository = baselineRepository;
        this.paths = paths;
        logger = LogManager.getLogger(PathRestController.class);
    }

    @GetMapping("/baselines/{id}/manifest")
    public ResponseEntity<String> getBaselineManifestPath(@PathVariable String id) {
        Optional<Baseline> baseline = baselineRepository.findById(id);
        return baseline.isPresent()
            ? ResponseEntity.ok(paths.local().baseline(baseline.get()).manifest().toString())
            : ResponseEntity.notFound().build();
    }

}
