package org.powertac.orchestrator.treatment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.powertac.orchestrator.baseline.Baseline;

@NoArgsConstructor
@AllArgsConstructor
public class TreatmentSpec {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Baseline baseline;

    @Getter
    @Setter
    private Modifier modifier;

}
