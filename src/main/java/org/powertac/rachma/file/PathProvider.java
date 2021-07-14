package org.powertac.rachma.file;

import org.powertac.rachma.game.ContextPathProvider;

public interface PathProvider {

    ContextPathProvider host();
    ContextPathProvider local();
    ContextPathProvider container();

}
