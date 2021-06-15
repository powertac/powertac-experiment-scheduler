package org.powertac.rachma.api.rest;

import lombok.Getter;
import org.apache.commons.lang.NotImplementedException;
import org.powertac.rachma.experiment.Baseline;
import org.powertac.rachma.experiment.BaselineRepository;
import org.powertac.rachma.instance.InstanceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("baseline")
public class BaselineRestController {

    private final BaselineRepository baselines;

    public BaselineRestController(BaselineRepository baselines) {
        this.baselines = baselines;
    }

    @GetMapping("/")
    public ResponseEntity<?> getBaselines() {
        return ResponseEntity.status(HttpStatus.OK).body(baselines.findAll());
    }

    @PostMapping("/")
    public ResponseEntity<?> createBaseline(@RequestBody Baseline newBaseline) {
        if (null != newBaseline.getId()) {
            Optional<Baseline> baseline = baselines.findById(newBaseline.getId());
            if (baseline.isPresent()) {
                // TODO : update instances
                // TODO : add Instance persistence
                throw new NotImplementedException("updating baselines is not yet implemented");
            }
            return badRequest(String.format("could not find existing baseline with id='%s'", newBaseline.getId()));
        } else {
            newBaseline.setId(UUID.randomUUID().toString());
            for (InstanceImpl instance : newBaseline.getInstances()) {
                instance.setId(UUID.randomUUID().toString());
            }
            baselines.save(newBaseline);
            return ResponseEntity.status(HttpStatus.OK).body(newBaseline);
        }
    }

    private ResponseEntity<?> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Object() {
            @Getter private final String error = message;
        });
    }

}
