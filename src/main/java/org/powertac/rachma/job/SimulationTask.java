package org.powertac.rachma.job;

import lombok.Getter;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.resource.WorkDirectory;
import org.powertac.rachma.task.AbstractTask;

import java.util.Map;
import java.util.Set;

@Deprecated
public class SimulationTask extends AbstractTask implements ServerTask {

    @Getter
    private final Set<BrokerType> brokers;

    @Getter
    private final Map<String, String> parameters;

    @Getter
    private final String bootstrapFilePath;

    @Getter
    private final String seedFilePath;

    public SimulationTask(String id, Job job, Set<BrokerType> brokers, Map<String, String> parameters) {
        super(id, job);
        this.brokers = brokers;
        this.parameters = parameters;
        this.bootstrapFilePath = null;
        this.seedFilePath = null;
    }

    public SimulationTask(String id, Job job, Set<BrokerType> brokers, Map<String, String> parameters,
                          String bootstrapFilePath, String seedFilePath) {
        super(id, job);
        this.brokers = brokers;
        this.parameters = parameters;
        this.bootstrapFilePath = bootstrapFilePath;
        this.seedFilePath = seedFilePath;
    }

    @Override
    public WorkDirectory getWorkDirectory() {
        return WorkDirectory.fromParent(job.getWorkDirectory(), String.format("sim.%s", id));
    }
}
