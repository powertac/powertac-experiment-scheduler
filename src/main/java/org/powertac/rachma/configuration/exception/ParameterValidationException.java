package org.powertac.rachma.configuration.exception;

import lombok.Getter;
import org.powertac.rachma.validation.exception.ValidationException;

public class ParameterValidationException extends ValidationException {

    @Getter
    private final String parameter;

    public ParameterValidationException(String parameter, String value, String message) {
        super(value, message);
        this.parameter = parameter;
    }

}
