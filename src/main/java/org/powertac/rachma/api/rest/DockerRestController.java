package org.powertac.rachma.api.rest;

import org.powertac.rachma.docker.DockerImage;
import org.powertac.rachma.docker.DockerImageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("docker")
public class DockerRestController {

    private final DockerImageRepository images;

    public DockerRestController(DockerImageRepository images) {
        this.images = images;
    }

    @GetMapping("/images/")
    public ResponseEntity<Collection<DockerImage>> getImages() {
        return ResponseEntity.ok(images.findAll());
    }

}
