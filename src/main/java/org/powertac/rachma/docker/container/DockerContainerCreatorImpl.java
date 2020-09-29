package org.powertac.rachma.docker.container;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.*;
import org.powertac.rachma.docker.exception.NotFoundException;
import org.powertac.rachma.docker.network.ApiDrivenDockerNetworkRepository;
import org.powertac.rachma.docker.network.DockerNetwork;
import org.powertac.rachma.docker.network.DockerNetworkConfig;
import org.powertac.rachma.resource.SharedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DockerContainerCreatorImpl implements DockerContainerCreator {

    private final DockerClient dockerClient;
    private final ApiDrivenDockerNetworkRepository networkRepository;

    @Autowired
    public DockerContainerCreatorImpl(DockerClient dockerClient, ApiDrivenDockerNetworkRepository networkRepository) {
        this.dockerClient = dockerClient;
        this.networkRepository = networkRepository;
    }

    public DockerContainer createContainer(DockerContainerSpec spec) throws DockerException {

        String imageTag = spec.getImage();
        HostConfig hostConfig = createHostConfig(spec.getSharedFiles(), spec.getNetworkConfig());
        CreateContainerCmd createCommand = dockerClient.createContainerCmd(imageTag);

        if (null != spec.getName()) {
            createCommand.withName(spec.getName());
        }

        if (null != spec.getCommand()) {
            createCommand.withCmd(spec.getCommand().toList());
        }

        if (!spec.getNetworkConfig().getExposedPorts().isEmpty()) {
            List<ExposedPort> exposedPorts = getExposedPorts(spec.getNetworkConfig());
            createCommand.withExposedPorts(exposedPorts);
        }

        if (!spec.getNetworkConfig().getContainerAliases().isEmpty()) {
            createCommand.withAliases(new ArrayList<>(spec.getNetworkConfig().getContainerAliases()));
        }

        CreateContainerResponse containerCreateResponse = createCommand.withHostConfig(hostConfig).exec();

        return new DockerContainer(containerCreateResponse.getId(), spec.getName());
    }

    private List<ExposedPort> getExposedPorts(DockerNetworkConfig config) {
        return config.getExposedPorts().stream()
            .map(port -> new ExposedPort(port, InternetProtocol.TCP))
            .collect(Collectors.toList());
    }

    private HostConfig createHostConfig(Set<SharedFile> sharedFiles, DockerNetworkConfig dockerNetworkConfig) throws DockerException {

        HostConfig hostConfig = new HostConfig();

        List<Bind> binds = createBinds(sharedFiles);
        hostConfig.withBinds(binds);

        if (null != dockerNetworkConfig.getNetwork()) {
            DockerNetwork network = findOrCreateNetwork(dockerNetworkConfig.getNetwork());
            hostConfig.withNetworkMode(network.getId());
        }

        return hostConfig;
    }

    private List<Bind> createBinds(Set<SharedFile> files) {
        return files.stream()
            .map(file -> new Bind(file.getHostPath(), new Volume(file.getContainerPath())))
            .collect(Collectors.toList());
    }

    private DockerNetwork findOrCreateNetwork(String name) throws DockerException {
        try {
            return networkRepository.findByName(name);
        }
        catch (NotFoundException e) {
            return networkRepository.createNetwork(name);
        }
    }

}
