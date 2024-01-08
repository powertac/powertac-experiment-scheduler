package org.powertac.orchestrator.api.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;

@RestController
public class ApplicationRestController {

    @Value("${directory.host.export}")
    private String hostExportBasePath;

    @GetMapping("/paths/export")
    public ResponseEntity<String> getHostExportBasePath() {
        return ResponseEntity.ok(Paths.get(hostExportBasePath).toAbsolutePath().toString() + '/');
    }

}
