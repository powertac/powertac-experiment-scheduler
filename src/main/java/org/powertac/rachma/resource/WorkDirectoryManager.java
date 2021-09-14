package org.powertac.rachma.resource;

import org.powertac.rachma.job.Job;
import org.powertac.rachma.util.InternalResourceExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Deprecated
public class WorkDirectoryManager {

    @Value("${directory.local.base}")
    private String baseDirectory;

    @Value("${directory.local.jobs}")
    private String localJobDirectory;

    @Value("${directory.host.jobs}")
    private String hostJobDirectory;

    @Value("${directory.local.brokers}")
    private String brokerDirectory;

    @Value("${directory.local.services}")
    private String servicesDirectory;

    private final InternalResourceExporter resourceExporter;

    public WorkDirectoryManager(InternalResourceExporter resourceExporter) {
        this.resourceExporter = resourceExporter;
    }

    // TODO : remove since this mixes up concepts... these are two concepts with the same name
    public void createMainDirectoriesAndCopyResourceFiles() throws IOException {
        createDirectoryIfNotExists(baseDirectory);
        createDirectoryIfNotExists(localJobDirectory);
        resourceExporter.exportDirectory("/brokers", brokerDirectory);
        // TODO : only server.properties is needed for running the orchestrator; therefore the other files should be omitted
        resourceExporter.exportDirectory("/services", servicesDirectory);
    }

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
