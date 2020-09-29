package org.powertac.rachma.experiment;

import com.google.common.hash.Hashing;
import org.powertac.rachma.hash.HashProvider;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.treatment.Treatment;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class Sha256ExperimentHashProvider implements HashProvider<Experiment> {

    private final HashProvider<Instance> instanceHashProvider;
    private final HashProvider<Treatment> treatmentHashProvider;

    public Sha256ExperimentHashProvider(HashProvider<Instance> instanceHashProvider,
                                        HashProvider<Treatment> treatmentHashProvider) {
        this.instanceHashProvider = instanceHashProvider;
        this.treatmentHashProvider = treatmentHashProvider;
    }

    @Override
    public String getHash(Experiment experiment) {
        Stream<String> baselineHashes = experiment.getBaseline().stream()
            .map(instanceHashProvider::getHash);
        Stream<String> treatmentHashes = experiment.getTreatments().stream()
            .map(treatmentHashProvider::getHash);
        String concatenatedHashes = Stream.concat(baselineHashes, treatmentHashes)
            .reduce("", (a,b) -> a + b);
        return Hashing.sha256().hashString(concatenatedHashes, StandardCharsets.UTF_8).toString();
    }

}
