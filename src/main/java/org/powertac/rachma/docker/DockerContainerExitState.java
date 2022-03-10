package org.powertac.rachma.docker;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DockerContainerExitState { // FIXME : might be replaced with int :D

    @Getter
    private final int exitCode;

    public boolean isErrorState() {
        return exitCode != 0;
    }

}
