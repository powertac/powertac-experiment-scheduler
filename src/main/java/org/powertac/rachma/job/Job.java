package org.powertac.rachma.job;

import org.powertac.rachma.resource.WorkDirectory;

import java.nio.file.Path;
import java.util.Map;

@Deprecated
public interface Job {
    void setId(String id);
    String getId();
    String getName();
    JobStatus getStatus();
    void setWorkDirectory(WorkDirectory dir);
    WorkDirectory getWorkDirectory();
    Map<String, Path> getFiles();
}
