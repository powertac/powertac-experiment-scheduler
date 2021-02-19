package org.powertac.rachma.job;

import org.powertac.rachma.experiment.Experiment;
import org.powertac.rachma.resource.WorkDirectory;
import org.powertac.rachma.runner.RunnableEntity;

import java.nio.file.Path;
import java.util.Map;

public interface Job extends RunnableEntity {
    void setId(String id);
    String getId();
    String getName();
    JobStatus getStatus();
    void setWorkDirectory(WorkDirectory dir);
    WorkDirectory getWorkDirectory();
    Map<String, Path> getFiles();
    void setExperiment(Experiment experiment);
    Experiment getExperiment();
}
