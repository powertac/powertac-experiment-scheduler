package org.powertac.rachma.docker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DockerContainerExitState {

    @Getter
    private final int exitCode;

    public boolean isErrorState() {
        return exitCode != 0;
    }

}
