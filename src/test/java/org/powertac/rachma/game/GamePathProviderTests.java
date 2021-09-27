package org.powertac.rachma.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileRole;
import org.powertac.rachma.file.PathContext;
import org.powertac.rachma.file.PathContextType;

public class GamePathProviderTests {

    @Test
    void hostGamePathTest() {
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/def556",
            paths.dir().toString());
    }

    @Test
    void localGamePathTest() {
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/def556",
            paths.dir().toString());
    }

    @Test
    void containerGamePathTest() {
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertNull(paths.dir());
    }

    @Test
    void hostBrokersPathTest() {
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/def556/brokers",
            paths.brokers().toString());
    }

    @Test
    void localBrokersPathTest() {
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/def556/brokers",
            paths.brokers().toString());
    }

    @Test
    void containerBrokersPathTest() {
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertNull(paths.brokers());
    }

    @Test
    void hostLogsPathTest() {
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/def556/log",
            paths.logs().toString());
    }

    @Test
    void localLogsPathTest() {
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/def556/log",
            paths.logs().toString());
    }

    @Test
    void containerLogsPathTest() {
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertEquals(
            "/powertac-server/log",
            paths.logs().toString());
    }

    @Test
    void hostStateLogPathTest() {
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/def556/log/powertac-sim-0.state",
            paths.stateLog().toString());
    }

    @Test
    void localStateLogPathTest() {
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/def556/log/powertac-sim-0.state",
            paths.stateLog().toString());
    }

    @Test
    void containerStateLogPathTest() {
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertEquals(
            "/powertac-server/log/powertac-sim-0.state",
            paths.stateLog().toString());
    }

    @Test
    void hostTraceLogPathTest() {
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/def556/log/powertac-sim-0.trace",
            paths.traceLog().toString());
    }

    @Test
    void localTraceLogPathTest() {
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/def556/log/powertac-sim-0.trace",
            paths.traceLog().toString());
    }

    @Test
    void containerTraceLogPathTest() {
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertEquals(
            "/powertac-server/log/powertac-sim-0.trace",
            paths.traceLog().toString());
    }

    @Test
    void hostSimulationPropertiesPathTest() {
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/def556/def556.simulation.properties",
            paths.properties().toString());
    }

    @Test
    void localSimulationPropertiesPathTest() {
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/def556/def556.simulation.properties",
            paths.properties().toString());
    }

    @Test
    void containerSimulationPropertiesPathTest() {
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertEquals(
            "/powertac/def556.simulation.properties",
            paths.properties().toString());
    }

    @Test
    void hostDefaultBootstrapPathTest() {
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/def556/def556.bootstrap.xml",
            paths.bootstrap().toString());
    }

    @Test
    void localDefaultBootstrapPathTest() {
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/def556/def556.bootstrap.xml",
            paths.bootstrap().toString());
    }

    @Test
    void containerDefaultBootstrapPathTest() {
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertEquals(
            "/powertac/def556.bootstrap.xml",
            paths.bootstrap().toString());
    }

    @Test
    void hostTransitiveBootstrapPathTest() {
        Game otherGame = Mockito.mock(Game.class);
        Mockito.when(otherGame.getId()).thenReturn("other999");
        File bootstrap = new File("aaaaaa", FileRole.BOOTSTRAP, otherGame);
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/", bootstrap, null);
        Assertions.assertEquals(
            "/host/path/to/games/other999/other999.bootstrap.xml",
            paths.bootstrap().toString());
    }

    @Test
    void localTransitiveBootstrapPathTest() {
        Game otherGame = Mockito.mock(Game.class);
        Mockito.when(otherGame.getId()).thenReturn("other999");
        File bootstrap = new File("aaaaaa", FileRole.BOOTSTRAP, otherGame);
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/", bootstrap, null);
        Assertions.assertEquals(
            "/local/path/to/games/other999/other999.bootstrap.xml",
            paths.bootstrap().toString());
    }

    @Test
    void containerTransitiveBootstrapPathTest() {
        Game otherGame = Mockito.mock(Game.class);
        Mockito.when(otherGame.getId()).thenReturn("other999");
        File bootstrap = new File("aaaaaa", FileRole.BOOTSTRAP, otherGame);
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/", bootstrap, null);
        Assertions.assertEquals(
            "/powertac/other999.bootstrap.xml",
            paths.bootstrap().toString());
    }

    @Test
    void hostDefaultSeedPathTest() {
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertNull(paths.seed());
    }

    @Test
    void localDefaultSeedPathTest() {
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertNull(paths.seed());
    }

    @Test
    void containerDefaultSeedPathTest() {
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertNull(paths.seed());
    }

    @Test
    void hostTransitiveSeedPathTest() {
        Game otherGame = Mockito.mock(Game.class);
        Mockito.when(otherGame.getId()).thenReturn("seed777");
        File seed = new File("bbbbbb", FileRole.SEED, otherGame);
        GamePathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/", null, seed);
        Assertions.assertEquals(
            "/host/path/to/games/seed777/log/powertac-sim-0.state",
            paths.seed().toString());
    }

    @Test
    void localTransitiveSeedPathTest() {
        Game otherGame = Mockito.mock(Game.class);
        Mockito.when(otherGame.getId()).thenReturn("seed777");
        File seed = new File("bbbbbb", FileRole.SEED, otherGame);
        GamePathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/", null, seed);
        Assertions.assertEquals(
            "/local/path/to/games/seed777/log/powertac-sim-0.state",
            paths.seed().toString());
    }

    @Test
    void containerTransitiveSeedPathTest() {
        Game otherGame = Mockito.mock(Game.class);
        Mockito.when(otherGame.getId()).thenReturn("seed777");
        File seed = new File("bbbbbb", FileRole.SEED, otherGame);
        GamePathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/", null, seed);
        Assertions.assertEquals(
            "/powertac/seed777.state",
            paths.seed().toString());
    }

    private GamePathProvider getProvider(PathContextType type, String root) {
        return getProvider(type, root, null, null);
    }

    private GamePathProvider getProvider(PathContextType type, String root, File bootstrap, File seed) {
        Game game = Mockito.mock(Game.class);
        Mockito.when(game.getId()).thenReturn("def556");
        Mockito.when(game.getBootstrap()).thenReturn(bootstrap);
        Mockito.when(game.getSeed()).thenReturn(seed);
        PathContext hostContext = new PathContext(type, root);
        return new GamePathProvider(hostContext, game);
    }

}
