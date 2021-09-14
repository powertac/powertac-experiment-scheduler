package org.powertac.rachma.validation;

public interface SimulationParameterValidator {

    void validate(String key, String value) throws ParameterValidationException;

}
