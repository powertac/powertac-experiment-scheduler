package org.powertac.orchestrator.paths;

import java.nio.file.Path;

public interface PathTranslator {

    Path toHost(Path original);
    Path toLocal(Path original);

}
