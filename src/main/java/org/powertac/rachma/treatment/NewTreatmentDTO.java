package org.powertac.rachma.treatment;

import lombok.Getter;

public class NewTreatmentDTO {

    @Getter
    private String name;

    @Getter
    private String baselineId;

    @Getter
    private NewModifierDTO modifier;

}
