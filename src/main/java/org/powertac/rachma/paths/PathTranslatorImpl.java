package org.powertac.rachma.paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PathTranslatorImpl implements PathTranslator {

    @Value("${directory.host.base}")
    private String hostBase;

    @Value("${directory.local.base}")
    private String localBase;

    @Override
    public Path toHost(Path original) {
        return Paths.get(original.toString().replace(localBase, hostBase));
    }

    @Override
    public Path toLocal(Path original) {
        return Paths.get(original.toString().replace(hostBase, localBase));
    }

}
