package testutils;

import org.mockito.Mockito;
import org.powertac.orchestrator.broker.Broker;
import org.powertac.orchestrator.game.Game;
import org.powertac.orchestrator.game.GameRun;

public class Mocker {

    public static Game game(String id) {
        Game game = Mockito.mock(Game.class);
        Mockito.when(game.getId()).thenReturn(id);
        return game;
    }

    public static GameRun run() {
        return Mockito.mock(GameRun.class);
    }

    public static GameRun run(String id, Game game) {
        GameRun run = Mockito.mock(GameRun.class);
        Mockito.when(run.getId()).thenReturn(id);
        Mockito.when(run.getGame()).thenReturn(game);
        return run;
    }

    public static Broker broker(String name) {
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getName()).thenReturn(name);
        Mockito.when(broker.getHumanReadableIdentifier()).thenReturn(name);
        return broker;
    }

}
