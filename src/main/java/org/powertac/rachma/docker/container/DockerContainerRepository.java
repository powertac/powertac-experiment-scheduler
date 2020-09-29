package org.powertac.rachma.docker.container;

import com.github.dockerjava.api.exception.DockerException;

import java.util.Set;

public interface DockerContainerRepository {

    void add(DockerContainer container);
    Set<DockerContainer> findAll();
    void remove(DockerContainer container) throws DockerException;
    void remove(DockerContainer container, boolean force) throws DockerException;
    boolean exists(DockerContainer container) throws DockerException;

}
