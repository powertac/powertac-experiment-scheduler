package org.powertac.rachma.validation;

import java.util.Set;

public interface ValidationRuleProvider {

    Set<ValidationRule> get(String key);

}
