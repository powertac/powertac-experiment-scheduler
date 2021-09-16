package org.powertac.rachma.docker;

import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Image;
import org.powertac.rachma.docker.exception.NotFoundException;

import java.util.Collection;

public interface DockerImageRepository {

    void pull(String tag) throws DockerException, InterruptedException;
    boolean exists(String tag) throws DockerException;
    Collection<DockerImage> findAll();

}
