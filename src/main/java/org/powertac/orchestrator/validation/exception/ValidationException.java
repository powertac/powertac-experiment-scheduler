package org.powertac.orchestrator.validation.exception;

import lombok.Getter;

public class ValidationException extends Exception {

    @Getter
    private final Object value;

    public ValidationException(Object value, String message) {
        super(message);
        this.value = value;
    }

}
