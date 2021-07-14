package org.powertac.rachma.api.rest;

import org.powertac.rachma.configuration.exception.ParameterValidationException;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRepository;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.instance.InstanceDuplicator;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.job.JobScheduler;
import org.powertac.rachma.job.exception.JobSchedulingException;
import org.powertac.rachma.powertac.broker.exception.BrokerNotFoundException;
import org.powertac.rachma.powertac.simulation.SimulationJob;
import org.powertac.rachma.powertac.simulation.SimulationJobFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/games")
public class GameController {

    private final JobRepository jobRepository;
    private final JobScheduler jobScheduler;
    private final InstanceDuplicator instanceDuplicator;
    private final SimulationJobFactory simulationJobFactory;

    private final GameRepository games;

    public GameController(JobRepository jobRepository, JobScheduler jobScheduler, InstanceDuplicator instanceDuplicator, SimulationJobFactory simulationJobFactory, GameRepository games) {
        this.jobRepository = jobRepository;
        this.jobScheduler = jobScheduler;
        this.instanceDuplicator = instanceDuplicator;
        this.simulationJobFactory = simulationJobFactory;
        this.games = games;
    }

    @PostMapping("/")
    public ResponseEntity<String> queueInstance(@RequestBody Instance game) throws BrokerNotFoundException, ParameterValidationException, IOException, JobSchedulingException {
        Instance instance = instanceDuplicator.createCopy(game); // FIXME : currently required to set ID; fix in 0.1.2
        SimulationJob job = simulationJobFactory.create(instance);
        jobRepository.add(job);
        jobScheduler.schedule(job);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/")
    public ResponseEntity<Collection<Game>> getGames() {
        return ResponseEntity.ok().body(games.findAll());
    }

    @PostMapping("/")
    public ResponseEntity<String> queueInstance(@RequestBody Game game) {
        games.save(game);
        return ResponseEntity.ok().build();
    }

}
