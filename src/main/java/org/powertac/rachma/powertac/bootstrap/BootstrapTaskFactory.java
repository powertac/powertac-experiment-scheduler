package org.powertac.rachma.powertac.bootstrap;

import org.powertac.rachma.job.Job;

public interface BootstrapTaskFactory {

    BootstrapTask create(Job job);

}
