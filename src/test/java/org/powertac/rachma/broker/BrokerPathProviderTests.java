package org.powertac.rachma.broker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.file.PathContext;
import org.powertac.rachma.file.PathContextType;
import org.powertac.rachma.game.Game;

public class BrokerPathProviderTests {

    @Test
    void hostBrokerDirPathTest() {
        BrokerPathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/xyz123/brokers/BROKER",
            paths.dir().toString());
    }

    @Test
    void hostPropertiesPathTest() {
        BrokerPathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/xyz123/brokers/BROKER/xyz123.BROKER.properties",
            paths.properties().toString());
    }

    @Test
    void hostSharedDirectoryPathTest() {
        BrokerPathProvider paths = getProvider(PathContextType.HOST, "/host/path/to/");
        Assertions.assertEquals(
            "/host/path/to/games/xyz123/brokers/BROKER/shared",
            paths.shared().toString());
    }

    @Test
    void localBrokerDirPathTest() {
        BrokerPathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/xyz123/brokers/BROKER",
            paths.dir().toString());
    }

    @Test
    void localPropertiesPathTest() {
        BrokerPathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/xyz123/brokers/BROKER/xyz123.BROKER.properties",
            paths.properties().toString());
    }

    @Test
    void localSharedDirectoryPathTest() {
        BrokerPathProvider paths = getProvider(PathContextType.LOCAL, "/local/path/to/");
        Assertions.assertEquals(
            "/local/path/to/games/xyz123/brokers/BROKER/shared",
            paths.shared().toString());
    }

    @Test
    void containerBrokerDirPathTest() {
        BrokerPathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertEquals(
            "/powertac/broker",
            paths.dir().toString());
    }

    @Test
    void containerPropertiesPathTest() {
        BrokerPathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertEquals(
            "/powertac/broker/xyz123.BROKER.properties",
            paths.properties().toString());
    }

    @Test
    void containerSharedDirectoryPathTest() {
        BrokerPathProvider paths = getProvider(PathContextType.CONTAINER, "/powertac/");
        Assertions.assertEquals(
            "/powertac/broker/shared",
            paths.shared().toString());
    }

    private BrokerPathProvider getProvider(PathContextType type, String root) {
        Game game = Mockito.mock(Game.class);
        Mockito.when(game.getId()).thenReturn("xyz123");
        Broker broker = new BrokerImpl("BROKER", "latest");
        PathContext hostContext = new PathContext(type, root);
        return new BrokerPathProvider(hostContext, game, broker);
    }

}
