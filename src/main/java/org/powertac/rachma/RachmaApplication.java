package org.powertac.rachma;

import org.powertac.rachma.docker.network.DockerNetworkCleaner;
import org.powertac.rachma.job.JobSchedulerInitializer;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class RachmaApplication implements ApplicationRunner, ApplicationContextAware {

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
        SpringApplication app = new SpringApplication(RachmaApplication.class);
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
        final JobSchedulerInitializer schedulerInitializer = context.getBean(JobSchedulerInitializer.class);
        final DockerNetworkCleaner networkCleaner = context.getBean(DockerNetworkCleaner.class);
        final ScheduledExecutorService networkCleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        schedulerInitializer.initialize();
        networkCleanupExecutor.scheduleAtFixedRate(networkCleaner::removeOrphanedNetworks, 0, 30, TimeUnit.MINUTES);
    }

}
