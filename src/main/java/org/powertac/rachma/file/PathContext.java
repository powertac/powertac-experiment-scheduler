package org.powertac.rachma.file;

import lombok.Getter;

public class PathContext {

    @Getter
    private final PathContextType type;

    @Getter
    private final String root;

    public PathContext(PathContextType type, String root) {
        this.type = type;
        this.root = root;
    }
}
