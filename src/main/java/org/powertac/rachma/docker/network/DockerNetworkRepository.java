package org.powertac.rachma.docker.network;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.exception.NotFoundException;

public interface DockerNetworkRepository {

    DockerNetwork createNetwork(String name) throws DockerException;
    DockerNetwork findByName(String name) throws NotFoundException, DockerException;
    void removeNetwork(DockerNetwork network) throws DockerException;

}
