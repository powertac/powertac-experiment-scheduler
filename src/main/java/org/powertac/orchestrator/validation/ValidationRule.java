package org.powertac.orchestrator.validation;

import org.powertac.orchestrator.validation.exception.ValidationException;

public interface ValidationRule {

    void validate(Object o) throws ValidationException;

}
