package org.powertac.rachma.docker.image;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Set;

@Service
public class DockerImageBuilderImpl implements DockerImageBuilder {

    private final DockerClient dockerClient;

    @Autowired
    public DockerImageBuilderImpl(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public String buildImage(String dockerFilePath, Set<String> tags) throws DockerException {
        return dockerClient.buildImageCmd()
            .withDockerfile(new File(dockerFilePath))
            .withTags(tags)
            .exec(new BuildImageResultCallback())
            .awaitImageId();
    }

}
