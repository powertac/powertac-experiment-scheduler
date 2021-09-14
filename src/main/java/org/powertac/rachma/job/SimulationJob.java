package org.powertac.rachma.job;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.powertac.rachma.job.serialization.JobSerializer;
import org.powertac.rachma.resource.WorkDirectory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = JobSerializer.class)
@Deprecated
public class SimulationJob extends AbstractJob implements Job {

    @Getter
    @Setter
    private WorkDirectory workDirectory;

    @Getter
    @Setter
    private BootstrapTask bootstrapTask;

    @Getter
    @Setter
    private SimulationTask simulationTask;

    @Override
    public Map<String, Path> getFiles() {
        Map<String, Path> files = new HashMap<>();
        Path bootstrapFile;
        if (null != simulationTask.getBootstrapFilePath()) {
            bootstrapFile = Path.of(simulationTask.getBootstrapFilePath());
        } else {
            bootstrapFile = getJobFile(String.format("%s.bootstrap.xml", getId()));
        }
        files.put("bootstrap-file", bootstrapFile);
        files.put("bootstrap-properties", getJobFile(String.format("%s.bootstrap.properties", getId())));
        files.put("simulation-properties", getJobFile(String.format("%s.simulation.properties", getId())));
        files.put("state-log", getJobFile("log/powertac-sim-0.state"));
        files.put("trace-log", getJobFile("log/powertac-sim-0.trace"));
        if (null != simulationTask.getSeedFilePath()) {
            files.put("seed-file", Path.of(simulationTask.getSeedFilePath()));
        }
        return files;
    }

    private Path getJobFile(String relativePath) {
        return Paths.get(String.format("%s/%s", workDirectory.getLocalDirectory(), relativePath)).toAbsolutePath();
    }

}
