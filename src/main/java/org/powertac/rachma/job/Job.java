package org.powertac.rachma.job;

import org.powertac.rachma.resource.WorkDirectory;
import org.powertac.rachma.runner.RunnableEntity;

public interface Job extends RunnableEntity {

    void setId(String id);

    String getId();

    String getName();

    JobStatus getStatus();

    void setWorkDirectory(WorkDirectory dir);

    WorkDirectory getWorkDirectory();

}
