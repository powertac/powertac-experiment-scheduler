package org.powertac.rachma.application;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cli extends Options {

    public static CommandLine parse(String[] args) throws ParseException {
        Cli cli = new Cli();
        cli.configureOptions();
        CommandLineParser parser = new DefaultParser();
        return parser.parse(cli, args);
    }

    private void configureOptions() {
        this.addOption("m", "migrate", true, "run specified migration");
        this.addOption("f", "force", false, "force the specified action");
    }

}
