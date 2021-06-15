package org.powertac.rachma.experiment;

import lombok.Getter;
import lombok.Setter;
import org.powertac.rachma.instance.InstanceImpl;
import org.springframework.data.annotation.Id;

import java.util.Set;

public class Baseline {

    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Set<InstanceImpl> instances;

}
