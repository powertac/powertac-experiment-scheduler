package org.powertac.rachma.resource;

public class SharedDirectoryImpl extends SharedFileImpl implements SharedDirectory {

    public SharedDirectoryImpl(String localPath, String hostPath, String containerPath) {
        super(localPath, hostPath, containerPath);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
}
