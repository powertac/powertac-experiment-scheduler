package org.powertac.rachma.powertac.server;

import lombok.AllArgsConstructor;
import org.powertac.rachma.docker.DockerContainerCommand;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ServerDockerContainerCommand implements DockerContainerCommand {

    public enum COMMAND {

        BOOT("boot"),
        SIM("sim");

        COMMAND(String command) {
            this.command = command;
        }

        private String command;

        public String getCommand() {
            return command;
        }

    }

    private final COMMAND command;
    private String inputFile;
    private String outputFile;
    private String propertyFile;
    private List<String> brokers;

    @Override
    public List<String> toList() {

        List<String> dockerCommand = new ArrayList<>();
        dockerCommand.add(command.getCommand());

        // general command options
        if (null != propertyFile) {
            dockerCommand.add("-c");
            dockerCommand.add(propertyFile);
        }

        // boot command options
        if (null != outputFile && command == ServerDockerContainerCommand.COMMAND.BOOT) {
            dockerCommand.add("-o");
            dockerCommand.add(outputFile);
        }

        // simulation command options
        if (null != inputFile && command == ServerDockerContainerCommand.COMMAND.SIM) {
            dockerCommand.add("-f");
            dockerCommand.add(inputFile);
        }

        if (!brokers.isEmpty() && command == ServerDockerContainerCommand.COMMAND.SIM) {
            dockerCommand.add("-b");
            dockerCommand.add(String.join(",", brokers));
        }

        return dockerCommand;
    }
}
