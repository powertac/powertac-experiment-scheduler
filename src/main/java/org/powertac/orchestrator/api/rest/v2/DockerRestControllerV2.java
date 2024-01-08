package org.powertac.orchestrator.api.rest.v2;

import org.powertac.orchestrator.docker.DockerImage;
import org.powertac.orchestrator.docker.DockerImageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/docker")
public class DockerRestControllerV2 {

    private final DockerImageRepository images;

    public DockerRestControllerV2(DockerImageRepository images) {
        this.images = images;
    }

    @GetMapping("/image-tags/")
    private ResponseEntity<Collection<String>> getAllImageTags() {
        return ResponseEntity.ok(images.findAll().stream()
            .map(DockerImage::getTag)
            .collect(Collectors.toSet()));
    }

}
