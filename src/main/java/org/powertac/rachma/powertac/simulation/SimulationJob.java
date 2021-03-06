package org.powertac.rachma.powertac.simulation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.powertac.rachma.experiment.Experiment;
import org.powertac.rachma.job.AbstractJob;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.serialization.JobSerializer;
import org.powertac.rachma.powertac.bootstrap.BootstrapTask;
import org.powertac.rachma.resource.WorkDirectory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize(using = JobSerializer.class)
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

    @Getter
    @Setter
    private Experiment experiment;

    @Override
    public Map<String, Path> getFiles() {
        Map<String, Path> files = new HashMap<>();
        files.put("bootstrap-file", getJobFile(String.format("%s.bootstrap.xml", getId())));
        files.put("bootstrap-properties", getJobFile(String.format("%s.bootstrap.properties", getId())));
        files.put("simulation-properties", getJobFile(String.format("%s.simulation.properties", getId())));
        files.put("state-log", getJobFile("log/powertac-sim-0.state"));
        files.put("trace-log", getJobFile("log/powertac-sim-0.trace"));
        return files;
    }

    private Path getJobFile(String relativePath) {
        return Paths.get(String.format("%s/%s", workDirectory.getLocalDirectory(), relativePath)).toAbsolutePath();
    }

}
