package org.powertac.rachma.docker.network;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateNetworkResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Network;
import org.powertac.rachma.docker.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApiDrivenDockerNetworkRepository implements DockerNetworkRepository {

    private static final String defaultDriver = "bridge";

    private final DockerClient dockerClient;
    private final Map<String, DockerNetwork> managedNetworks = new HashMap<>();

    @Autowired
    public ApiDrivenDockerNetworkRepository(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public DockerNetwork createNetwork(String name) throws DockerException {
        CreateNetworkResponse response = dockerClient.createNetworkCmd()
            .withDriver(defaultDriver)
            .withName(name)
            .exec();
        DockerNetwork network = new DockerNetwork(response.getId(), name);
        managedNetworks.put(name, network);
        return network;
    }

    public DockerNetwork findByName(String name) throws NotFoundException, DockerException {
        DockerNetwork network = dockerClient.listNetworksCmd()
            .withNameFilter(name)
            .exec().stream()
            .map(Network::getName)
            .filter(managedNetworks::containsKey)
            .map(managedNetworks::get)
            .findFirst().orElse(null);
        if (null != network) {
            return network;
        }
        throw new NotFoundException(String.format("No network found with name '%s'", name));
    }

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
