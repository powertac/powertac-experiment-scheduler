package org.powertac.rachma.api.rest;

import lombok.Getter;
import org.powertac.rachma.application.ApplicationStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;

@RestController
public class ApplicationRestController {

    @Value("${directory.host.export}")
    private String hostExportBasePath;

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

    @GetMapping("/paths/export")
    public ResponseEntity<String> getHostExportBasePath() {
        return ResponseEntity.ok(Paths.get(hostExportBasePath).toAbsolutePath().toString() + '/');
    }

}
