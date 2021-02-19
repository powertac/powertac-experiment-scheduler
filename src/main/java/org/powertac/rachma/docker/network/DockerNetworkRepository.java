package org.powertac.rachma.docker.network;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.exception.NotFoundException;

import java.util.Set;

public interface DockerNetworkRepository {

    Set<DockerNetwork> findAll() throws DockerException;
    DockerNetwork createNetwork(String name) throws DockerException;
    DockerNetwork findByName(String name) throws NotFoundException, DockerException;
    void removeNetwork(DockerNetwork network) throws DockerException;

}
