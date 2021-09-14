package org.powertac.rachma.validation;

import org.powertac.rachma.validation.exception.ValidationException;

import java.util.Set;

public class ServerParameterValidator implements SimulationParameterValidator {

    private final ValidationRuleProvider ruleProvider;
    private final Set<String> supportedParameters;

    public ServerParameterValidator(ValidationRuleProvider ruleProvider, Set<String> supportedParameters) {
        this.ruleProvider = ruleProvider;
        this.supportedParameters = supportedParameters;
    }

    @Override
    public void validate(String parameter, String value) throws ParameterValidationException {
        try {
            if (!this.supportedParameters.contains(parameter)) {
                throw new ParameterValidationException(parameter, value, String.format(
                    "'%s' is not a supported simulation server parameter", parameter));
            }
            for (ValidationRule rule : ruleProvider.get(parameter)) {
                rule.validate(value);
            }
        }
        catch (ValidationException e) {
            throw new ParameterValidationException(parameter, e.getValue().toString(), e.getMessage());
        }
    }

}
