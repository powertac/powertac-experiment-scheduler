package org.powertac.orchestrator.paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class OrchestratorPathsTests {

    @Test
    public void gamesDirectoryPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Assertions.assertEquals(
            Paths.get("/base/path/games"),
            paths.games());
    }

}
