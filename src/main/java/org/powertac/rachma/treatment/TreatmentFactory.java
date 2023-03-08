package org.powertac.rachma.treatment;

import org.powertac.rachma.baseline.Baseline;

public interface TreatmentFactory {

    Treatment createFrom(TreatmentSpec spec);
    Treatment create(String name, Baseline baseline, Modifier modifierDTO);

}
