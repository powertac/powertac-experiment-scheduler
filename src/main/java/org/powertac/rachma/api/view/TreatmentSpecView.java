package org.powertac.rachma.api.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import org.powertac.rachma.treatment.Modifier;
import org.powertac.rachma.treatment.ModifierDeserializer;

public class TreatmentSpecView {

    @Getter
    private String name;

    @Getter
    private String baselineId;

    @Getter
    @JsonDeserialize(using = ModifierDeserializer.class)
    private Modifier modifier;

}
