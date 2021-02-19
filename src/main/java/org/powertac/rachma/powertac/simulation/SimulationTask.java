package org.powertac.rachma.powertac.simulation;

import lombok.Getter;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.powertac.server.ServerTask;
import org.powertac.rachma.resource.WorkDirectory;
import org.powertac.rachma.task.AbstractTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimulationTask extends AbstractTask implements ServerTask {

    @Getter
    private final Set<BrokerType> brokers;

    @Getter
    private final Map<String, String> parameters;

    public SimulationTask(String id, Job job, Set<BrokerType> brokers) {
        super(id, job);
        this.brokers = brokers;
        this.parameters = new HashMap<>();
    }

    public SimulationTask(String id, Job job, Set<BrokerType> brokers, Map<String, String> parameters) {
        super(id, job);
        this.brokers = brokers;
        this.parameters = parameters;
    }

    @Override
    public WorkDirectory getWorkDirectory() {
        return WorkDirectory.fromParent(job.getWorkDirectory(), String.format("sim.%s", id));
    }

}
