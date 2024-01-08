package org.powertac.orchestrator.validation;

public interface SimulationParameterValidator {

    void validate(String key, String value) throws ParameterValidationException;

}
