package org.powertac.rachma.server;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class EntryScriptServerContainerCommandCreator implements ServerContainerCommandCreator {

    @Override
    public List<String> createBootstrapCommand(String propertiesFilePath, String bootstrapFilePath) {
        List<String> command = new ArrayList<>();
        command.add("boot");
        command.add("-c");
        command.add(propertiesFilePath);
        command.add("-o");
        command.add(bootstrapFilePath);
        return command;
    }

    @Override
    public List<String> createSimulationCommand(String propertiesFilePath, String bootstrapFilePath, String seedFilePath, Set<String> brokerNames) {
        List<String> command = new ArrayList<>();
        command.add("sim");
        command.add("-c");
        command.add(propertiesFilePath);
        command.add("-f");
        command.add(bootstrapFilePath);
        if (null != seedFilePath) {
            command.add("-s");
            command.add(seedFilePath);
        }
        command.add("-b");
        command.add(String.join(",", brokerNames));
        // TODO : add game id flag
        return command;
    }
    
}
