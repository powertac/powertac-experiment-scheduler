package org.powertac.orchestrator.treatment;

import org.powertac.orchestrator.baseline.Baseline;

public interface TreatmentFactory {

    @Deprecated Treatment createFrom(TreatmentSpec spec);
    Treatment create(String name, Baseline baseline, Modifier modifierDTO);

}
