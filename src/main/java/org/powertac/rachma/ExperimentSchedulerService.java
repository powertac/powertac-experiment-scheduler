package org.powertac.rachma;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.application.ApplicationSetup;
import org.powertac.rachma.application.LockException;
import org.powertac.rachma.exec.PersistentTaskRepository;
import org.powertac.rachma.exec.Task;
import org.powertac.rachma.exec.TaskExecutor;
import org.powertac.rachma.exec.TaskScheduler;
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
import java.util.Optional;
import java.util.concurrent.Executors;
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
    public void run(ApplicationArguments args) throws Exception {
        runStartupTasks();
        runGames();
        runTasks();
    }

    private void runStartupTasks() throws SeederException, LockException, IOException {
        context.getBean(ApplicationSetup.class).start();
        context.getBean(SeederManager.class).runSeeders();
    }

    private void runGames() {
        final GameScheduler scheduler = context.getBean(GameScheduler.class);
        final Logger logger = LogManager.getLogger(ExperimentSchedulerService.class);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                scheduler.runGames();
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void runTasks() {
        final TaskScheduler taskScheduler = context.getBean(TaskScheduler.class);
        final TaskExecutor<Task> taskExecutor = (TaskExecutor<Task>) context.getBean("taskExecutor");
        final Logger logger = LogManager.getLogger("task runner");
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            Optional<Task> task = taskScheduler.next();
            if (taskExecutor.hasCapacity() && task.isPresent()) {
                if (taskExecutor.accepts(task.get())) {
                    try {
                        taskExecutor.exec(task.get());
                    } catch (Exception e) {
                        logger.error("error during task execution", e);
                    }
                }
                logger.error("no executor configured for type " + task.getClass());
                // FIXME : fail task with unconfigured executor
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

}
