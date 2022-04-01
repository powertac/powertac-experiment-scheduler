package org.powertac.rachma.paths;

import java.nio.file.Path;

public interface PathTranslator {

    Path toHost(Path original);
    Path toLocal(Path original);

}
