package org.powertac.rachma.api.rest.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController("/v2/files")
public class FileRestControllerV2 {

    private final Logger logger;

    public FileRestControllerV2() {
        this.logger = LogManager.getLogger(FileRestControllerV2.class);
    }

    @GetMapping("/**")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getFile(HttpServletRequest request) {
        try {
            Path filePath = resolvePath(request);
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(new InputStreamResource(Files.newInputStream(filePath)));
        } catch (Exception e) {
            logger.error("unable to read file", e);
            return ResponseEntity.badRequest().build();
        }
    }

    private Path resolvePath(HttpServletRequest request) {
        return Paths.get(new AntPathMatcher().extractPathWithinPattern(
            request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString(),
            request.getRequestURI()));
    }



}
