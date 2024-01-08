package org.powertac.orchestrator.validation;

import java.util.Set;

public interface ValidationRuleProvider {

    Set<ValidationRule> get(String key);

}
