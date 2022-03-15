package org.powertac.rachma.application;

import org.apache.commons.cli.CommandLine;
import org.powertac.rachma.ExperimentSchedulerService;
import org.powertac.rachma.MigrationService;

public class ExperimentSchedulerLoader {

    public static void main(String[] args) {
        try {
            CommandLine cli = Cli.parse(args);
            if (cli.hasOption("migrate")) {
                MigrationService.main(args);
            } else {
                ExperimentSchedulerService.main(args);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

}
