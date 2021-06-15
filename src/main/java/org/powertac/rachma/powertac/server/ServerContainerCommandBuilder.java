package org.powertac.rachma.powertac.server;

import java.util.ArrayList;
import java.util.List;

public class ServerContainerCommandBuilder {

    private final ServerDockerContainerCommand.COMMAND command;
    private String bootstrapFile;
    private String outputFile;
    private String propertyFile;
    private String seedFile;
    private List<String> brokers = new ArrayList<>();

    public static ServerContainerCommandBuilder sim() {
        return ServerContainerCommandBuilder.command(ServerDockerContainerCommand.COMMAND.SIM);
    }

    public static ServerContainerCommandBuilder boot() {
        return ServerContainerCommandBuilder.command(ServerDockerContainerCommand.COMMAND.BOOT);
    }

    public static ServerContainerCommandBuilder command(ServerDockerContainerCommand.COMMAND command) {
        return new ServerContainerCommandBuilder(command);
    }

    private ServerContainerCommandBuilder(ServerDockerContainerCommand.COMMAND command) {
        this.command = command;
    }

    public ServerContainerCommandBuilder withBootstrapFile(String inputFile) {
        this.bootstrapFile = inputFile;
        return this;
    }

    public ServerContainerCommandBuilder withOutputFile(String outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public ServerContainerCommandBuilder withPropertyFile(String propertyFile) {
        this.propertyFile = propertyFile;
        return this;
    }

    public ServerContainerCommandBuilder withBrokers(List<String> brokers) {
        this.brokers = brokers;
        return this;
    }

    public ServerContainerCommandBuilder withSeedFile(String seedFile) {
        this.seedFile = seedFile;
        return this;
    }

    public ServerDockerContainerCommand build() {
        return new ServerDockerContainerCommand(
            command,
            bootstrapFile,
            outputFile,
            propertyFile,
            brokers,
            seedFile
        );
    }

}
