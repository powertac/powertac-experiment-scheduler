package org.powertac.rachma.application;

public enum DeploymentContext {

    HOST("host"),
    CONTAINER("container");

    private final String label;

    DeploymentContext(String label) {
        this.label = label;
    }

    public String getSize() {
        return label;
    }

}
