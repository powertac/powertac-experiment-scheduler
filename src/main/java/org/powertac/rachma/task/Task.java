package org.powertac.rachma.task;

import org.powertac.rachma.job.Job;
import org.powertac.rachma.resource.WorkDirectory;

import java.util.Map;

@Deprecated
public interface Task {

    String getId();

    TaskStatus getStatus();

    Job getJob();

    WorkDirectory getWorkDirectory();

    // TODO : redundancy : there is also the ConfigurableTask interface
    Map<String, String> getParameters();

}
