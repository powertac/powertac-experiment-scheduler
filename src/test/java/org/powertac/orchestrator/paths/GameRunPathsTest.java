package org.powertac.orchestrator.paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;
import testutils.Mocker;

import java.nio.file.Paths;

public class GameRunPathsTest {

    @Test
    void runDirPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path/");
        Game game = Mocker.game("76bf48de-d16d-45da-93cd-4589890b3932");
        GameRun run = Mocker.run("8d62eb30-cf76-4a39-bed1-caef22c3f9c0", game);
        Assertions.assertEquals(
            Paths.get("/base/path/games/76bf48de-d16d-45da-93cd-4589890b3932/runs/8d62eb30-cf76-4a39-bed1-caef22c3f9c0"),
            paths.run(run).dir());
    }

    @Test
    void logPathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path/");
        Game game = Mocker.game("59084ae7-66f3-4a7a-89cd-f3a32e449587");
        GameRun run = Mocker.run("a9e66bbe-7fe4-47df-b259-7f30aea1dc5a", game);
        Assertions.assertEquals(
            Paths.get("/base/path/games/59084ae7-66f3-4a7a-89cd-f3a32e449587/runs/a9e66bbe-7fe4-47df-b259-7f30aea1dc5a/a9e66bbe-7fe4-47df-b259-7f30aea1dc5a.run.log"),
            paths.run(run).log());
    }

    @Test
    void statePathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path/");
        Game game = Mocker.game("ce5b4f16-d64c-4780-a169-d28b7111d441");
        GameRun run = Mocker.run("5656688d-e22e-4993-a99a-e1f91554e36a", game);
        Assertions.assertEquals(
            Paths.get("/base/path/games/ce5b4f16-d64c-4780-a169-d28b7111d441/runs/5656688d-e22e-4993-a99a-e1f91554e36a/server/5656688d-e22e-4993-a99a-e1f91554e36a.state"),
            paths.run(run).state());
    }

    @Test
    void tracePathTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Game game = Mocker.game("1d2fc786-a31e-4af1-a59f-bce2aeb45528");
        GameRun run = Mocker.run("9f7e775c-06ab-4f53-9ee5-5ddb17366bf1", game);
        Assertions.assertEquals(
            Paths.get("/base/path/games/1d2fc786-a31e-4af1-a59f-bce2aeb45528/runs/9f7e775c-06ab-4f53-9ee5-5ddb17366bf1/server/9f7e775c-06ab-4f53-9ee5-5ddb17366bf1.trace"),
            paths.run(run).trace());
    }

    @Test
    void brokerDirTest() {
        PathProvider.OrchestratorPaths paths = new OrchestratorPathsImpl("/base/path");
        Game game = Mocker.game("c6547052-3bf1-4d91-8786-9f91230297f1");
        GameRun run = Mocker.run("8e3394ae-726c-4d66-969e-addf76371ebf", game);
        Broker broker = Mocker.broker("Galen");
        Assertions.assertEquals(
            Paths.get("/base/path/games/c6547052-3bf1-4d91-8786-9f91230297f1/runs/8e3394ae-726c-4d66-969e-addf76371ebf/Galen"),
            paths.run(run).broker(broker).dir());
    }

}
