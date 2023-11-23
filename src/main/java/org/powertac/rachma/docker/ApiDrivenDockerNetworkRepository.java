package org.powertac.rachma.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
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

    @Override
    public void removeNetworkIfExists(String name) throws DockerException {
        try {
            dockerClient.removeNetworkCmd(name).exec();
            managedNetworks.remove(name);
        } catch (NotFoundException e) {
            // suppress exception due to non-existent network
        }
    }

    @Override
    public boolean exists(String networkIdOrName) throws DockerException {
        try {
            dockerClient.inspectNetworkCmd()
                .withNetworkId(networkIdOrName)
                .exec();
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @PreDestroy
    public void cleanUpNetworks() {
        for (DockerNetwork network : managedNetworks.values()) {
            removeNetwork(network);
        }
    }

}
