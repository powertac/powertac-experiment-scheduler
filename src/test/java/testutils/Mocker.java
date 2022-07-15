package testutils;

import org.mockito.Mockito;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.game.Game;
import org.powertac.rachma.game.GameRun;

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
        return broker;
    }

}