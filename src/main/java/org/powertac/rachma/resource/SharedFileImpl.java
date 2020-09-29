package org.powertac.rachma.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SharedFileImpl implements SharedFile {

    @Getter
    private final String localPath;

    @Getter
    private final String hostPath;

    @Getter
    private final String containerPath;

    @Override
    public boolean isDirectory() {
        return false;
    }
}
