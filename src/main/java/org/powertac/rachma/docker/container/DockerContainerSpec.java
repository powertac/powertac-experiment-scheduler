package org.powertac.rachma.docker.container;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.powertac.rachma.docker.DockerContainerCommand;
import org.powertac.rachma.docker.network.DockerNetworkConfig;
import org.powertac.rachma.resource.SharedDirectory;
import org.powertac.rachma.resource.SharedFile;
import org.powertac.rachma.runner.RunnableEntity;

import java.util.HashSet;
import java.util.Set;

@Builder
public class DockerContainerSpec implements RunnableEntity {

    @Getter
    private String image;

    @Getter
    private String name;

    @Getter
    private DockerContainerCommand command;

    @Getter
    private String network;

    @Getter
    @Singular
    private Set<String> aliases;

    @Getter
    @Singular
    private Set<Integer> exposedPorts;

    @Getter
    @Singular
    private Set<SharedFile> files;

    @Getter
    @Singular
    private Set<SharedDirectory> directories;

    public Set<SharedFile> getSharedFiles() {
        Set<SharedFile> bindables = new HashSet<>();
        bindables.addAll(files);
        bindables.addAll(directories);
        return bindables;
    }

    public DockerNetworkConfig getNetworkConfig() {
        DockerNetworkConfig.DockerNetworkConfigBuilder builder = DockerNetworkConfig.builder();
        if (null != network) {
            builder.network(network);
        }
        return builder
            .exposedPorts(exposedPorts)
            .containerAliases(aliases)
            .build();
    }

}
