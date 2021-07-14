package org.powertac.rachma.file;

import org.powertac.rachma.game.ContextPathProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurablePathProvider implements PathProvider {

    private final static String containerRoot = "/powertac/";

    @Value("${directory.host.base}")
    private String hostRoot;

    @Value("${directory.local.base}")
    private String localRoot;

    public ContextPathProvider host() {
        return new ContextPathProvider(PathContextType.HOST, hostRoot);
    }

    public ContextPathProvider local() {
        return new ContextPathProvider(PathContextType.LOCAL, localRoot);
    }

    public ContextPathProvider container() {
        return new ContextPathProvider(PathContextType.CONTAINER, containerRoot);
    }

}
