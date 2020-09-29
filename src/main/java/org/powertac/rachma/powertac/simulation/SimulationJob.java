package org.powertac.rachma.powertac.simulation;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import org.powertac.rachma.job.AbstractJob;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.serialization.JobSerializer;
import org.powertac.rachma.powertac.bootstrap.BootstrapTask;
import org.powertac.rachma.resource.WorkDirectory;

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

}
