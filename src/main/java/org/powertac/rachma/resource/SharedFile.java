package org.powertac.rachma.resource;

public interface SharedFile {

    String getLocalPath();
    String getHostPath();
    String getContainerPath();
    boolean isDirectory();

}
