package org.powertac.orchestrator.paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testutils.Mocker;

import java.nio.file.Paths;

public class ServerPathsTests {

    @Test
    void basePathTest() {
        PathProvider.ContainerPaths paths = new ContainerPathsImpl();
        Assertions.assertEquals(
            Paths.get("/powertac/server"),
            paths.server().base());
    }

    @Test
    void bootstrapPathTest() {
        PathProvider.ContainerPaths paths = new ContainerPathsImpl();
        Assertions.assertEquals(
            Paths.get("/powertac/server/7e36994a-2de8-4cf7-ade0-659c71f18744.bootstrap.xml"),
            paths.server().game(Mocker.game("7e36994a-2de8-4cf7-ade0-659c71f18744")).bootstrap());
    }

    @Test
    void propertiesPathTest() {
        PathProvider.ContainerPaths paths = new ContainerPathsImpl();
        Assertions.assertEquals(
            Paths.get("/powertac/server/09034ebc-ac2f-4297-ad01-019eccb2645a.server.properties"),
            paths.server().game(Mocker.game("09034ebc-ac2f-4297-ad01-019eccb2645a")).properties());
    }

    @Test
    void seedPathTest() {
        PathProvider.ContainerPaths paths = new ContainerPathsImpl();
        Assertions.assertEquals(
            Paths.get("/powertac/server/04de93a5-536e-4d72-885d-9079991663c9.seed.state"),
            paths.server().game(Mocker.game("04de93a5-536e-4d72-885d-9079991663c9")).seed());
    }

    @Test
    void statePathTest() {
        PathProvider.ContainerPaths paths = new ContainerPathsImpl();
        Assertions.assertEquals(
            Paths.get("/powertac/server/log/powertac-sim-0.state"),
            paths.server().run(Mocker.run()).state());
    }

    @Test
    void tracePathTest() {
        PathProvider.ContainerPaths paths = new ContainerPathsImpl();
        Assertions.assertEquals(
            Paths.get("/powertac/server/log/powertac-sim-0.trace"),
            paths.server().run(Mocker.run()).trace());
    }

}
