package org.powertac.orchestrator.validation;

import lombok.Getter;
import org.powertac.orchestrator.validation.exception.ValidationException;

public class ParameterValidationException extends ValidationException {

    @Getter
    private final String parameter;

    public ParameterValidationException(String parameter, String value, String message) {
        super(value, message);
        this.parameter = parameter;
    }

}
