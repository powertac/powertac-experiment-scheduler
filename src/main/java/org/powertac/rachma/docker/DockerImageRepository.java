package org.powertac.rachma.docker;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.exception.NotFoundException;

public interface DockerImageRepository {

    void pull(String tag) throws DockerException, InterruptedException;
    boolean exists(String tag) throws DockerException;

}
