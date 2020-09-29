package org.powertac.rachma.resource;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalSourceSharedFileDriver implements SharedFileDriver {

    @Override
    public boolean exists(SharedFile file) {
        Path sourcePath = getSourcePath(file);
        return Files.exists(sourcePath);
    }

    @Override
    public void create(SharedFile file) throws IOException {
        Path sourcePath = getSourcePath(file);
        if (file instanceof SharedDirectory) {
            createDirectory(sourcePath);
        }
        else {
            createDirectory(sourcePath.getParent());
            createFile(sourcePath);
        }
    }

    private void createDirectory(Path path) throws IOException {
        Files.createDirectories(path.getParent());
    }

    private void createFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }

    private Path getSourcePath(SharedFile file) {
        return Paths.get(file.getLocalPath());
    }

}
