package org.powertac.orchestrator.treatment;

public enum ModifierType {

    REPLACE_BROKER("replace-broker"),
    PARAMETER_SET("parameter-set");

    public final String label;

    ModifierType(String label) {
        this.label = label;
    }

}
