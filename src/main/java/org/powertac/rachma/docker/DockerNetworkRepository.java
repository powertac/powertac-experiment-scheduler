package org.powertac.rachma.docker;

import com.github.dockerjava.api.exception.DockerException;
import org.powertac.rachma.docker.exception.NotFoundException;

import java.util.Set;

public interface DockerNetworkRepository {

    Set<DockerNetwork> findAll() throws DockerException;
    DockerNetwork createNetwork(String name) throws DockerException;
    void removeNetwork(DockerNetwork network) throws DockerException;
    void removeNetworkIfExists(String name) throws DockerException;

}
