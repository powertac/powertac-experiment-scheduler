package org.powertac.rachma.resource;

import lombok.Getter;

import java.io.File;

// TODO : merge with sharedDirectory ... because its basically the same...
// TODO : ... or just get rid of this concept... in the end it's clumsy and somewhat overengineered
@Deprecated
public class WorkDirectory {

    @Getter
    private final String localDirectory;

    @Getter
    private final String hostDirectory;

    public static WorkDirectory fromParent(WorkDirectory parent, String suffix) {
        return new WorkDirectory(
            parent.getLocalDirectory() + File.separator + suffix,
            parent.getHostDirectory() + File.separator + suffix);
    }

    public WorkDirectory(String localDirectory, String hostDirectory) {
        this.localDirectory = localDirectory;
        this.hostDirectory = hostDirectory;
    }

}
