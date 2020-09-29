package org.powertac.rachma.powertac.server;

import java.util.ArrayList;
import java.util.List;

public class ServerContainerCommandBuilder {

    private final ServerDockerContainerCommand.COMMAND command;
    private String inputFile;
    private String outputFile;
    private String propertyFile;
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

    public ServerContainerCommandBuilder withInputFile(String inputFile) {
        this.inputFile = inputFile;
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

    public ServerContainerCommandBuilder withBroker(String broker) {
        brokers.add(broker);
        return this;
    }

    public ServerContainerCommandBuilder clearBrokers() {
        this.brokers = new ArrayList<>();
        return this;
    }

    public ServerDockerContainerCommand build() {
        return new ServerDockerContainerCommand(
            command,
            inputFile,
            outputFile,
            propertyFile,
            brokers
        );
    }

}
