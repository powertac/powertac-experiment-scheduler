package org.powertac.rachma.docker;

import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;

import java.nio.file.Path;

public class AbstractBindFactory {

    protected Bind bind(Path hostPath, Path containerPath) {
        return new Bind(
            hostPath.toString(),
            new Volume(containerPath.toString()));
    }

}
