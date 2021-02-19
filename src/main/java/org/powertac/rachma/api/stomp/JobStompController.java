package org.powertac.rachma.api.stomp;

import org.powertac.rachma.api.request.CreateJobRequest;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.JobScheduler;
import org.powertac.rachma.powertac.simulation.SimulationJob;
import org.powertac.rachma.powertac.simulation.SimulationJobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * @deprecated use REST controller instead
 * @see org.powertac.rachma.api.rest.GameController
 */
@Deprecated
@Controller
public class JobStompController {

    private final SimulationJobFactory simulationJobFactory;
    private final JobRepository jobRepository;
    private final JobScheduler jobScheduler;

    @Autowired
    public JobStompController(JobRepository jobRepository, JobScheduler jobScheduler,
                              SimulationJobFactory simulationJobFactory) {
        this.jobRepository = jobRepository;
        this.jobScheduler = jobScheduler;
        this.simulationJobFactory = simulationJobFactory;
    }

    @MessageMapping("/jobs/create")
    public void createJob(CreateJobRequest request) throws Exception {
        SimulationJob newJob = simulationJobFactory.create(
            request.getName(),
            request.getBrokers(),
            request.getParams());
        jobRepository.add(newJob);
        jobScheduler.schedule(newJob);
    }

}
