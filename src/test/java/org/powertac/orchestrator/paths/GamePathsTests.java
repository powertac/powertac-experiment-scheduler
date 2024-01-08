package org.powertac.orchestrator.paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.file.File;
import org.powertac.orchestrator.file.FileRole;
import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;
import testutils.Mocker;

import java.nio.file.Paths;
import java.util.HashSet;

public class GamePathsTests {

    @Test
    public void bootstrapPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Assertions.assertEquals(
            Paths.get("/base/path/games/b4c33dd4-7c89-4ea7-80a1-9221613c9490/b4c33dd4-7c89-4ea7-80a1-9221613c9490.bootstrap.xml"),
            paths.game(Mocker.game("b4c33dd4-7c89-4ea7-80a1-9221613c9490")).bootstrap());
    }

    @Test
    public void gameDirPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Assertions.assertEquals(
            Paths.get("/base/path/games/5f1de1c9-9728-41df-8956-70fe54a2fd72"),
            paths.game(Mocker.game("5f1de1c9-9728-41df-8956-70fe54a2fd72")).dir());
    }

    @Test
    public void gameRunsDirPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Assertions.assertEquals(
            Paths.get("/base/path/games/da52e230-7743-4286-a4c5-ce46ae6527c5/runs"),
            paths.game(Mocker.game("da52e230-7743-4286-a4c5-ce46ae6527c5")).runs());
    }

    @Test
    public void propertiesPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Assertions.assertEquals(
            Paths.get("/base/path/games/a64cbc75-02a4-468c-ab7c-6021360400d3/a64cbc75-02a4-468c-ab7c-6021360400d3.server.properties"),
            paths.game(Mocker.game("a64cbc75-02a4-468c-ab7c-6021360400d3")).properties());
    }

    @Test
    public void seedPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Game game = Mockito.mock(Game.class);
        Game seedGame = Mocker.game("2f44b6f9-ddd1-41a5-85e7-6c6c0b850b32");
        Mockito.when(game.getSeed()).thenReturn(new File(
            null,
            FileRole.SEED,
            seedGame,
            "",
            new HashSet<>()));
        GameRun run = Mockito.mock(GameRun.class);
        Mockito.when(run.getId()).thenReturn("08f5c1f9-6f50-4885-bf7c-79e50b448afe");
        Mockito.when(run.getGame()).thenReturn(seedGame);
        Mockito.when(seedGame.getLatestSuccessfulRun()).thenReturn(run);
        Assertions.assertEquals(
            Paths.get(
                "/base/path/games/",
                "2f44b6f9-ddd1-41a5-85e7-6c6c0b850b32",
                "runs",
                "08f5c1f9-6f50-4885-bf7c-79e50b448afe",
                "server",
                "08f5c1f9-6f50-4885-bf7c-79e50b448afe.state"),
            paths.game(game).seed());
    }

    @Test
    public void emptySeedPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Game game = Mocker.game("9a5ac081-1ac3-4583-9a93-622c9437bcb7");
        Mockito.when(game.getSeed()).thenReturn(null);
        Assertions.assertNull(paths.game(game).seed());
    }

    @Test
    void brokerPropertiesPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Game game = Mocker.game("73c3d2b0-5b26-414b-8cce-d13169969bec");
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getName()).thenReturn("IS-3");
        Assertions.assertEquals(
            Paths.get("/base/path/games/73c3d2b0-5b26-414b-8cce-d13169969bec/73c3d2b0-5b26-414b-8cce-d13169969bec.IS-3.properties"),
            paths.game(game).broker(broker).properties());
    }

}
