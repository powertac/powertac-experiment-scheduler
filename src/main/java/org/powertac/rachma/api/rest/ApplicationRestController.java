package org.powertac.rachma.api.rest;

import lombok.Getter;
import org.powertac.rachma.application.ApplicationStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationRestController {

    private final ApplicationStatus applicationStatus;

    public ApplicationRestController(ApplicationStatus status) {
        this.applicationStatus = status;
    }

    @GetMapping("/status")
    public Object getStatus() {
        return new Object() {
            @Getter boolean success = true;
            @Getter Object payload = new Object() {
                @Getter boolean running = applicationStatus.getState().equals(ApplicationStatus.State.RUNNING);
                @Getter boolean healthy = applicationStatus.getInconsistencies().size() <= 0;
            };
        };
    }

}
