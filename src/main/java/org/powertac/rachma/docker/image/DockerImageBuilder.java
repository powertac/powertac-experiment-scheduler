package org.powertac.rachma.docker.image;

import com.github.dockerjava.api.exception.DockerException;

import java.util.Set;

public interface DockerImageBuilder {

    String buildImage(String dockerFilePath, Set<String> tags) throws DockerException;

}
