package org.powertac.rachma.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.powertac.rachma.TestHelper;
import org.powertac.rachma.broker.Broker;
import org.powertac.rachma.broker.BrokerDeserializer;
import org.powertac.rachma.file.File;
import org.powertac.rachma.file.FileDeserializer;
import org.powertac.rachma.file.FileRole;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// TODO : add failing tests
public class GameDeserializerTests {

    //@Test
    void idDeserializedCorrectlyTest() throws IOException {
        Game game = game();
        Assertions.assertEquals("xyz123abc098", game.getId());
    }

    //@Test
    void nameDeserializedCorrectlyTest() throws IOException {
        Game game = game();
        Assertions.assertEquals("TestGame", game.getName());
    }

    //@Test
    void brokersDeserializedCorrectlyTest() throws IOException {
        Broker ude = new Broker("AgentUDE", "2015");
        Broker ewi = new Broker("EWIIS3", "2020.1");
        Broker crocodile = new Broker("CrocodileAgent", "latest");
        Game game = game();
        Assertions.assertEquals(3, game.getBrokers().size());
        Assertions.assertTrue(game.getBrokers().contains(ude));
        Assertions.assertTrue(game.getBrokers().contains(ewi));
        Assertions.assertTrue(game.getBrokers().contains(crocodile));
    }

    //@Test
    void serverParametersDeserializedCorrectlyTest() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("accounting.accountingService.bankInterest", "0.05");
        params.put("common.competition.simulationBaseTime", "2010-06-06");
        Assertions.assertEquals(params, game().getServerParameters());
    }

    //@Test
    void bootstrapDeserializedCorrectlyTest() throws IOException {
        Game bootstrapFileGame = Mockito.mock(Game.class);
        Mockito.when(bootstrapFileGame.getId()).thenReturn("abcdefg123456");
        GameRepository gameRepository = Mockito.mock(GameRepository.class);
        Mockito.when(gameRepository.findById("abcdefg123456")).thenReturn(bootstrapFileGame);
        Game game = game(gameRepository);
        Assertions.assertEquals(
            "abcdefg123456",
            game.getBootstrap().getGame().getId());
        Assertions.assertEquals(
            FileRole.BOOTSTRAP,
            game.getBootstrap().getRole());
    }

    //@Test
    void seedDeserializedCorrectlyTest() throws IOException {
        Game seedFileGame = Mockito.mock(Game.class);
        Mockito.when(seedFileGame.getId()).thenReturn("mnb678vcx345");
        GameRepository gameRepository = Mockito.mock(GameRepository.class);
        Mockito.when(gameRepository.findById("mnb678vcx345")).thenReturn(seedFileGame);
        Game game = game(gameRepository);
        Assertions.assertEquals(
            "mnb678vcx345",
            game.getSeed().getGame().getId());
        Assertions.assertEquals(
            FileRole.SEED,
            game.getSeed().getRole());
    }

    private Game game() throws IOException {
        GameRepository gameRepository = Mockito.mock(GameRepository.class);
        return game(gameRepository);
    }

    private Game game(GameRepository gameRepository) throws IOException {
        return getMapper(gameRepository).readValue(
            TestHelper.getContent("/game.json"),
            Game.class);
    }

    private ObjectMapper getMapper(GameRepository gameRepository) {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Game.class, new GameDeserializer());
        module.addDeserializer(Broker.class, new BrokerDeserializer());
        module.addDeserializer(File.class, new FileDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

}
