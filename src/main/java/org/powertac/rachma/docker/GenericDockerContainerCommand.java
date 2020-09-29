package org.powertac.rachma.docker;

import java.util.ArrayList;
import java.util.List;

public class GenericDockerContainerCommand implements DockerContainerCommand {

    @Override
    public List<String> toList() {
        return new ArrayList<>();
    }
}
