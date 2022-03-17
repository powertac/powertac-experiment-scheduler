package org.powertac.rachma.treatment;

public interface TreatmentFactory {

    Treatment createFrom(TreatmentSpec spec);

}
