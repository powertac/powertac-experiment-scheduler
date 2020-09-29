package org.powertac.rachma.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ValidationRuleRepository implements ValidationRuleProvider {

    private final Map<String, Set<ValidationRule>> rules = new HashMap<>();

    @Override
    public Set<ValidationRule> get(String key) {

        if (!rules.containsKey(key)) {
            return new HashSet<>();
        }

        return rules.get(key);
    }

    public void add(String key, ValidationRule rule) {

        if (!rules.containsKey(key)) {
            rules.put(key, new HashSet<>());
        }

        rules.get(key).add(rule);
    }

    public void remove(String key) {
        rules.remove(key);
    }

    public void clear() {
        rules.clear();
    }

}
