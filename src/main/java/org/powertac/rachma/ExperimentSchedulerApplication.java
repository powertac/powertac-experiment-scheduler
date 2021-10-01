package org.powertac.rachma;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.application.ApplicationSetup;
import org.powertac.rachma.application.LockException;
import org.powertac.rachma.game.GameScheduler;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication(exclude = {
    MongoAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
@EnableAspectJAutoProxy
@EnableJpaRepositories
public class ExperimentSchedulerApplication implements ApplicationRunner, ApplicationContextAware {

    private ApplicationContext context;

    public enum Mode {

        PRODUCTION("production"),
        DEVELOPMENT("development");

        private final String identifier;

        Mode(String identifier) {
            this.identifier = identifier;
        }

        public static Mode from(String identifier) {
            for (Mode mode : Mode.values()) {
                if (mode.identifier.equalsIgnoreCase(identifier)) {
                    return mode;
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExperimentSchedulerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
		app.run(args);
	}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        final ApplicationSetup setup = context.getBean(ApplicationSetup.class);
        try {
            setup.start();
        } catch (LockException e) {
            LogManager.getLogger(ExperimentSchedulerApplication.class).error("setup is already running", e);
        }
        final GameScheduler gameScheduler = context.getBean(GameScheduler.class);
        final Logger logger = LogManager.getLogger(ExperimentSchedulerApplication.class);
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
