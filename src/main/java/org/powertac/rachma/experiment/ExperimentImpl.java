package org.powertac.rachma.experiment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.treatment.Treatment;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ExperimentImpl implements Experiment {

    @Setter
    @Getter
    private String hash;

    @Getter
    private String name;

    @Getter
    private List<Instance> baseline;

    @Getter
    private List<Treatment> treatments;

    @Getter
    @Setter
    private List<Instance> instances;

    public ExperimentImpl(String name, List<Instance> baseline, List<Treatment> treatments) {
        this.name = name;
        this.baseline = baseline;
        this.treatments = treatments;
    }

    public ExperimentImpl(String hash, String name, List<Instance> baseline, List<Treatment> treatments) {
        this.hash = hash;
        this.name = name;
        this.baseline = baseline;
        this.treatments = treatments;
    }

}
