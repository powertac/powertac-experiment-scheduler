package org.powertac.rachma.api.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.powertac.rachma.api.request.CreateJobRequest;
import org.powertac.rachma.experiment.ExperimentRepository;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.JobScheduler;
import org.powertac.rachma.job.exception.JobNotFoundException;
import org.powertac.rachma.powertac.simulation.SimulationJob;
import org.powertac.rachma.powertac.simulation.SimulationJobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("jobs")
public class JobRestController {

    private final JobRepository jobRepository;
    private final JobScheduler jobScheduler;
    private final SimulationJobFactory simulationJobFactory;
    private final ObjectMapper mapper;
    private final ExperimentRepository experimentRepository;

    @Autowired
    public JobRestController(JobRepository jobRepository, JobScheduler jobScheduler, SimulationJobFactory simulationJobFactory, ObjectMapper mapper, ExperimentRepository experimentRepository) {
        this.jobRepository = jobRepository;
        this.jobScheduler = jobScheduler;
        this.simulationJobFactory = simulationJobFactory;
        this.mapper = mapper;
        this.experimentRepository = experimentRepository;
    }

    @PostMapping("/simulation")
    public Object createJob(@RequestBody CreateJobRequest jobRequest) {
        try {
            SimulationJob newJob = simulationJobFactory.create(jobRequest.getName(), jobRequest.getBrokers(), jobRequest.getParams());
            jobRepository.add(newJob);
            jobScheduler.schedule(newJob);
            return new Object() {
                @Getter final boolean success = true;
                @Getter final Job job = newJob;
            };
        }
        catch (Exception e) {
            return new  Object() {
                @Setter @Getter boolean success = false;
                @Setter @Getter String message = e.getMessage();
            };
        }
    }

    @GetMapping("/")
    public Object getJobs() {
        List<Job> jobs = jobRepository.findAll();
        // TODO : make experiment <-> job mapping explicit; 0.1.2 refactor
        for (Job job : jobs) {
            job.setExperiment(experimentRepository.findByInstanceId(job.getId()));
        }
        return new Object() {
            @Getter final boolean success = true;
            @Getter final List<Job> payload = jobs;
        };
    }

    @GetMapping("/supported-params")
    public Object getSupportedParams() {

        try {
            InputStream paramFileStream = getClass().getClassLoader().getResourceAsStream("editable-server-properties.names.json");
            BufferedReader paramFileReader = new BufferedReader(new InputStreamReader(paramFileStream));

            StringBuilder content = new StringBuilder();
            String line;

            while ((line = paramFileReader.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }

            List<String> params = mapper.readValue(content.toString(), new TypeReference<List<String>>(){});

            return new Object() {
                @Getter boolean success = true;
                @Getter List<String> payload = params;
            };
        }
        catch (IOException e) {
            return new Object() {
                @Getter boolean success = false;
                @Getter String message = e.getMessage();
            };
        }
    }

    @GetMapping("/{id}")
    public Object getJob(@PathVariable("id") String id) {
        try {
            return new Object() {
                @Getter boolean success = true;
                // TODO : this should be named payload
                @Getter Job job = jobRepository.find(id);
            };
        }
        catch (JobNotFoundException e) {
            return new Object() {
                @Getter boolean success = false;
                @Getter String message = e.getMessage();
            };
        }
    }

    @GetMapping("/{id}/log")
    public Object getJobLog(@PathVariable("id") String id) {
        try {

            Job job = jobRepository.find(id);

            File logFile = new File(job.getWorkDirectory().getLocalDirectory() + "/job." + job.getId() + ".log");

            if (!logFile.exists()) {
                return new Object() {
                    @Getter boolean success = false;
                    @Getter String message = "could not find log file for job with id=" + job.getId();
                };
            }

            Path path = Paths.get(logFile.getCanonicalPath());
            Charset charset = StandardCharsets.UTF_8;
            String jobLog = new String(Files.readAllBytes(path), charset);

            return new Object() {
                @Getter boolean success = true;
                @Getter String log = jobLog;
            };
        }
        catch (JobNotFoundException|IOException e) {
            return new Object() {
                @Getter boolean success = false;
                @Getter String message = e.getMessage();
            };
        }
    }
}
