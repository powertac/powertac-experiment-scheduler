package org.powertac.rachma.task;

import org.powertac.rachma.runner.RunnableEntity;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.resource.WorkDirectory;

import java.util.Map;

public interface Task extends RunnableEntity {

    String getId();

    TaskStatus getStatus();

    Job getJob();

    WorkDirectory getWorkDirectory();

    // TODO : redundancy : there is also the ConfigurableTask interface
    Map<String, String> getParameters();

}
