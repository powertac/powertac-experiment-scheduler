package org.powertac.rachma.instance;

import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.job.JobStatus;
import org.powertac.rachma.runner.RunnableEntity;

import java.util.Set;

public interface Instance extends RunnableEntity {

    String getId();
    String getName();
    ServerParameters getServerParameters();
    Set<Broker> getBrokers();
    JobStatus getStatus();

    // TODO : this is just a workaround for the time being
    void setStatus(JobStatus status);

}
