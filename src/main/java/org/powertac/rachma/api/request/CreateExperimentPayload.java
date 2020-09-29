package org.powertac.rachma.api.request;

import lombok.Getter;
import org.powertac.rachma.instance.Instance;
import org.powertac.rachma.treatment.Treatment;

import java.util.ArrayList;
import java.util.List;

public class CreateExperimentPayload {

    @Getter
    private String name;

    @Getter
    private List<Instance> baseline = new ArrayList<>();

    @Getter
    private List<Treatment> treatments = new ArrayList<>();

}
