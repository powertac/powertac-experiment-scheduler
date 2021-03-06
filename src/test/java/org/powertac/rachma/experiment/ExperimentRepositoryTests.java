package org.powertac.rachma.experiment;

import org.mockito.Mockito;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.job.JobRepository;
import org.powertac.rachma.treatment.Treatment;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

public class ExperimentRepositoryTests {

    // TODO : fix test
    // @Test
    void addPersistsExperimentTest() {
        MongoTemplate mongoRepository = Mockito.mock(MongoTemplate.class);
        JobRepository jobRepository = Mockito.mock(JobRepository.class);
        ExperimentRepository repository = new MongoFacadeExperimentRepository(mongoRepository, jobRepository);

        List<Instance> baseline = new ArrayList<>();
        List<Treatment> treatments = new ArrayList<>();

        Experiment experiment = new ExperimentImpl(
            "123456abcdef",
            "Klaus Peter Elfriede",
            baseline,
            treatments
        );

        repository.add(experiment);
        Mockito.verify(mongoRepository).save(experiment, "experiment");
    }

}
