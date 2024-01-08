package org.powertac.orchestrator.api.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import org.powertac.orchestrator.treatment.Modifier;
import org.powertac.orchestrator.treatment.ModifierDeserializer;

public class TreatmentSpecView {

    @Getter
    private String name;

    @Getter
    private String baselineId;

    @Getter
    @JsonDeserialize(using = ModifierDeserializer.class)
    private Modifier modifier;

}
