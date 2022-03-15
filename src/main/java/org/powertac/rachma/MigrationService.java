package org.powertac.rachma;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.application.ApplicationSetup;
import org.powertac.rachma.application.Cli;
import org.powertac.rachma.application.LockException;
import org.powertac.rachma.persistence.migration.Migration;
import org.powertac.rachma.persistence.migration.MigrationRunner;
import org.powertac.rachma.persistence.migration.NewGameModelMigration;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@EnableAutoConfiguration
@ComponentScan
@EnableAspectJAutoProxy
@EnableJpaRepositories
public class MigrationService implements ApplicationRunner, ApplicationContextAware {

    private final Logger logger;
    private ApplicationContext context;

    public MigrationService() {
        this.logger = LogManager.getLogger();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MigrationService.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Override
    public void run(ApplicationArguments args) {
        final ApplicationSetup setup = context.getBean(ApplicationSetup.class);
        try {
            setup.start();
        } catch (LockException e) {
            logger.error("setup is already running", e);
        } catch (IOException e) {
            logger.error("application setup failed", e);
        }
        try {
            CommandLine cli = Cli.parse(args.getSourceArgs());
            if (cli.hasOption("migrate")) {
                if (cli.getOptionValue("migrate").equals("baseline")) {
                    MigrationRunner runner = context.getBean(MigrationRunner.class);
                    Migration migration = context.getBean(NewGameModelMigration.class);
                    runner.registerMigration(migration);
                    runner.runMigrations();
                } else {
                    throw new IllegalArgumentException(
                        String.format("migration '%s' does not exist", cli.getOptionValue("migrate")));
                }
            } else {
                throw new MissingArgumentException("missing argument 'migrate'");
            }
        } catch (ParseException| IllegalArgumentException e) {
            logger.error(e);
            System.exit(1);
        }
    }

}
