package org.powertac.rachma.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Network;
import org.powertac.rachma.docker.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ApiDrivenDockerNetworkRepository implements DockerNetworkRepository {

    private static final String defaultDriver = "bridge";

    private final DockerClient dockerClient;
    private final Map<String, DockerNetwork> managedNetworks = new HashMap<>();

    @Autowired
    public ApiDrivenDockerNetworkRepository(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public Set<DockerNetwork> findAll() throws DockerException {
        return new HashSet<>(managedNetworks.values());
    }

    @Override
    public DockerNetwork createNetwork(String name) throws DockerException {
        CreateNetworkResponse response = dockerClient.createNetworkCmd()
            .withDriver(defaultDriver)
            .withName(name)
            .exec();
        DockerNetwork network = new DockerNetwork(response.getId(), name);
        managedNetworks.put(name, network);
        return network;
    }

    @Override
    public void removeNetwork(DockerNetwork network) throws DockerException {
        dockerClient.removeNetworkCmd(network.getId()).exec();
        managedNetworks.remove(network.getName());
    }

    @PreDestroy
    public void cleanUpNetworks() {
        for (DockerNetwork network : managedNetworks.values()) {
            removeNetwork(network);
        }
    }

}
