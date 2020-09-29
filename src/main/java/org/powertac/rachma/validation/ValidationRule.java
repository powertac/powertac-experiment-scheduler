package org.powertac.rachma.validation;

import org.powertac.rachma.validation.exception.ValidationException;

public interface ValidationRule {

    void validate(Object o) throws ValidationException;

}
