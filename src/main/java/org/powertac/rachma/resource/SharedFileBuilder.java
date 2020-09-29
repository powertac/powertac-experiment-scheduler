package org.powertac.rachma.resource;

import java.io.File;

public class SharedFileBuilder {

    private String localDirectory;
    private String hostDirectory;
    private String containerDirectory;
    private String relativePath;
    private boolean isDirectory = false;

    public static SharedFileBuilder create() {
        return new SharedFileBuilder();
    }

    public SharedFileBuilder localDirectory(String localDirectory) {
        this.localDirectory = localDirectory;
        return this;
    }

    public SharedFileBuilder hostDirectory(String hostDirectory) {
        this.hostDirectory = hostDirectory;
        return this;
    }

    public SharedFileBuilder containerDirectory(String containerDirectory) {
        this.containerDirectory = containerDirectory;
        return this;
    }

    public SharedFileBuilder file(String file) {
        this.relativePath = file;
        return this;
    }

    public SharedFileBuilder directory(String directory) {
        this.relativePath = directory;
        this.isDirectory = true;
        return this;
    }

    public SharedFile build() {
        return isDirectory
            ? buildSharedDirectory()
            : buildSharedFile();
    }

    private SharedDirectory buildSharedDirectory() {
        return new SharedDirectoryImpl(
            replaceDoubleSlashes(localDirectory + File.separator + relativePath),
            replaceDoubleSlashes(hostDirectory + File.separator + relativePath),
            replaceDoubleSlashes(containerDirectory + File.separator + relativePath)
        );
    }

    private SharedFile buildSharedFile() {
        return new SharedFileImpl(
            replaceDoubleSlashes(localDirectory + File.separator + relativePath),
            replaceDoubleSlashes(hostDirectory + File.separator + relativePath),
            replaceDoubleSlashes(containerDirectory + File.separator + relativePath)
        );
    }

    private String replaceDoubleSlashes(String path) {
        return path.replaceAll("/+", "/");
    }

}
