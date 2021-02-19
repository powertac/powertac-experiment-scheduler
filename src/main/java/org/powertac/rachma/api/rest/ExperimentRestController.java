package org.powertac.rachma.api.rest;

import lombok.Getter;
import org.powertac.rachma.api.request.CreateExperimentPayload;
import org.powertac.rachma.configuration.exception.ParameterValidationException;
import org.powertac.rachma.experiment.Experiment;
import org.powertac.rachma.experiment.ExperimentFactory;
import org.powertac.rachma.experiment.ExperimentRepository;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobFactory;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.JobScheduler;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;
import org.powertac.rachma.powertac.simulation.SimulationJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("experiments")
public class ExperimentRestController {

    private final ExperimentFactory experimentFactory;
    private final ExperimentRepository experimentRepository;
    private final JobFactory<SimulationJob> simulationJobJobFactory;
    private final JobRepository jobRepository;
    private final JobScheduler jobScheduler;

    @Autowired
    public ExperimentRestController(ExperimentFactory experimentFactory, ExperimentRepository experimentRepository,
                                    JobFactory<SimulationJob> simulationJobJobFactory, JobRepository jobRepository,
                                    JobScheduler jobScheduler) {
        this.experimentFactory = experimentFactory;
        this.experimentRepository = experimentRepository;
        this.simulationJobJobFactory = simulationJobJobFactory;
        this.jobRepository = jobRepository;
        this.jobScheduler = jobScheduler;
    }

    @GetMapping("/")
    public Object getExperiments() {
        List<Experiment> exps = experimentRepository.findAll();
        try {
            return new Object() {
                @Getter final boolean success = true;
                @Getter final List<Experiment> experiments = exps;
            };
        }
        catch (Exception e) {
            // TODO : send correct status code
            return new Object() {
                @Getter final boolean success = false;
                @Getter final String error = e.getMessage();
            };
        }
    }

    @PostMapping("/")
    public Object createExperiment(@RequestBody CreateExperimentPayload payload) {
        try {
            Experiment experiment = experimentFactory.create(
                payload.getName(),
                payload.getBaseline(),
                payload.getTreatments());
            experimentRepository.add(experiment);
            List<Job> jobs = createJobs(experiment.getInstances());
            jobRepository.addAll(jobs);
            jobScheduler.schedule(jobs);
            return new Object() {
                @Getter final boolean success = true;
            };
        }
        catch (Exception e) {
            // TODO : send correct status code
            return new Object() {
                @Getter final boolean success = false;
                @Getter final String error = e.getMessage();
            };
        }
    }

    private List<Job> createJobs(List<Instance> instances) throws BrokerNotFoundException, ParameterValidationException, IOException {
        List<Job> jobs = new ArrayList<>();
        for (Instance instance: instances) {
            jobs.add(simulationJobJobFactory.create(instance));
        }
        return jobs;
    }

}
