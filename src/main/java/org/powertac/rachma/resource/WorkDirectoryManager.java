package org.powertac.rachma.resource;

import org.powertac.rachma.job.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Deprecated
public class WorkDirectoryManager {

    @Value("${directory.local.jobs}")
    private String localJobDirectory;

    @Value("${directory.host.jobs}")
    private String hostJobDirectory;

    public WorkDirectory create(Job job) throws IOException {
        String localWorkDirectory = localJobDirectory + job.getId();
        createDirectoryIfNotExists(localWorkDirectory);
        return new WorkDirectory(localWorkDirectory, hostJobDirectory + job.getId());
    }

    private void createDirectoryIfNotExists(String directory) throws IOException {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

}
