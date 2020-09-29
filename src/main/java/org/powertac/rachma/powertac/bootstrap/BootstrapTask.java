package org.powertac.rachma.powertac.bootstrap;

import lombok.Getter;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.powertac.server.ServerTask;
import org.powertac.rachma.resource.WorkDirectory;
import org.powertac.rachma.task.AbstractTask;

import java.util.HashMap;
import java.util.Map;

public class BootstrapTask extends AbstractTask implements ServerTask {

    @Getter
    private final Map<String, String> parameters;

    public BootstrapTask(String id , Job job) {
        super(id, job);
        this.parameters = new HashMap<>();
    }

    public BootstrapTask(String id , Job job, Map<String, String> parameters) {
        super(id, job);
        this.parameters = parameters;
    }

    @Override
    public WorkDirectory getWorkDirectory() {
        return WorkDirectory.fromParent(job.getWorkDirectory(), String.format("boot.%s", id));
    }

}