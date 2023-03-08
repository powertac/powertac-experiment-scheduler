package org.powertac.rachma;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.application.ApplicationSetup;
import org.powertac.rachma.application.LockException;
import org.powertac.rachma.game.GameScheduler;
import org.powertac.rachma.persistence.SeederException;
import org.powertac.rachma.persistence.SeederManager;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@EnableAutoConfiguration(exclude = {
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
@ComponentScan
@EnableAspectJAutoProxy
@EnableJpaRepositories
public class ExperimentSchedulerService implements ApplicationRunner, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExperimentSchedulerService.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Override
    public void run(ApplicationArguments args) {
        final ApplicationSetup setup = context.getBean(ApplicationSetup.class);
        final SeederManager seeder = context.getBean(SeederManager.class);
        try {
            setup.start();
            seeder.runSeeders();
        } catch (LockException e) {
            LogManager.getLogger(ExperimentSchedulerService.class).error("setup is already running", e);
        } catch (IOException | SeederException e) {
            LogManager.getLogger(ExperimentSchedulerService.class).error("application setup failed", e);
        }
        final GameScheduler gameScheduler = context.getBean(GameScheduler.class);
        final Logger logger = LogManager.getLogger(ExperimentSchedulerService.class);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(runGames(gameScheduler, logger), 1, 1, TimeUnit.SECONDS);
    }

    private Runnable runGames(GameScheduler scheduler, Logger logger) {
        return () -> {
            try {
                scheduler.runGames();
            } catch (InterruptedException e) {
                logger.error(e);
            }
        };
    }

}
