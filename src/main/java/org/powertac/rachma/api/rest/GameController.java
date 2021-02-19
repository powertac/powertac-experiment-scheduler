package org.powertac.rachma.api.rest;

import org.powertac.rachma.configuration.exception.ParameterValidationException;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.instance.InstanceDuplicator;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.JobScheduler;
import org.powertac.rachma.job.exception.JobSchedulingException;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;
import org.powertac.rachma.powertac.simulation.SimulationJob;
import org.powertac.rachma.powertac.simulation.SimulationJobFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/games")
public class GameController {

    private final JobRepository jobRepository;
    private final JobScheduler jobScheduler;
    private final InstanceDuplicator instanceDuplicator;
    private final SimulationJobFactory simulationJobFactory;

    public GameController(JobRepository jobRepository, JobScheduler jobScheduler, InstanceDuplicator instanceDuplicator, SimulationJobFactory simulationJobFactory) {
        this.jobRepository = jobRepository;
        this.jobScheduler = jobScheduler;
        this.instanceDuplicator = instanceDuplicator;
        this.simulationJobFactory = simulationJobFactory;
    }

    @PostMapping("/")
    public ResponseEntity<String> queueInstance(@RequestBody Instance game) throws BrokerNotFoundException, ParameterValidationException, IOException, JobSchedulingException {
        Instance instance = instanceDuplicator.createCopy(game); // FIXME : currently required to set ID; fix in 0.1.2
        SimulationJob job = simulationJobFactory.create(instance);
        jobRepository.add(job);
        jobScheduler.schedule(job);
        return ResponseEntity.ok().build();
    }

}
