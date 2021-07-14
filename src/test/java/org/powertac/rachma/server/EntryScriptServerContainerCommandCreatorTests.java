package org.powertac.rachma.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntryScriptServerContainerCommandCreatorTests {

    @Test
    void bootstrapCommandTest() {
        ServerContainerCommandCreator commandCreator = new EntryScriptServerContainerCommandCreator();
        List<String> expected = new ArrayList<>();
        expected.add("boot");
        expected.add("-c");
        expected.add("/path/to/bootstrap.properties");
        expected.add("-o");
        expected.add("/path/to/bootstrap.xml");
        Assertions.assertEquals(expected, commandCreator.createBootstrapCommand(
            "/path/to/bootstrap.properties",
            "/path/to/bootstrap.xml"));
    }

    @Test
    void simulationCommandWithoutSeedTest() {
        ServerContainerCommandCreator commandCreator = new EntryScriptServerContainerCommandCreator();
        List<String> expected = new ArrayList<>();
        expected.add("sim");
        expected.add("-c");
        expected.add("/path/to/simulation.properties");
        expected.add("-f");
        expected.add("/path/to/bootstrap.xml");
        expected.add("-b");
        expected.add("TUC_TAC_2020,SPOT19,EWIIS3_2021");
        Assertions.assertEquals(expected, commandCreator.createSimulationCommand(
            "/path/to/simulation.properties",
            "/path/to/bootstrap.xml",
            null,
            Stream.of("EWIIS3_2021", "TUC_TAC_2020", "SPOT19").collect(Collectors.toSet())));
    }

}
