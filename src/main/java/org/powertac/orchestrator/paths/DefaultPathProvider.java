package org.powertac.orchestrator.paths;

public class DefaultPathProvider implements PathProvider {

    private final OrchestratorPaths hostPathProvider;
    private final OrchestratorPaths localPathProvider;
    private final ContainerPaths containerPathProvider;

    public DefaultPathProvider(String hostBasePath, String localBasePath) {
        hostPathProvider = new OrchestratorPathsImpl(hostBasePath);
        localPathProvider = new OrchestratorPathsImpl(localBasePath);
        containerPathProvider = new ContainerPathsImpl();
    }

    @Override
    public OrchestratorPaths host() {
        return hostPathProvider;
    }

    @Override
    public OrchestratorPaths local() {
        return localPathProvider;
    }

    @Override
    public ContainerPaths container() {
        return containerPathProvider;
    }

}
