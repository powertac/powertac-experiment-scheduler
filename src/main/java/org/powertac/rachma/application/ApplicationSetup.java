package org.powertac.rachma.application;

import com.github.dockerjava.api.exception.DockerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.broker.BrokerSeeder;
import org.powertac.rachma.docker.DockerImageBuilder;
import org.powertac.rachma.docker.DockerImageRepository;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.broker.BrokerTypeRepository;
import org.powertac.rachma.paths.PathProvider;
import org.powertac.rachma.persistence.MigrationRunner;
import org.powertac.rachma.persistence.SchemaViewSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ApplicationSetup {

    private final PathProvider paths;
    private final DockerImageBuilder dockerImageBuilder;
    private final DockerImageRepository imageRepository;
    private final BrokerTypeRepository brokerTypeRepository;
    private final BrokerSeeder brokerSeeder;
    private final SchemaViewSeeder viewSeeder;
    private final MigrationRunner migrationRunner;
    private final Logger logger;
    private final ApplicationStatus status;

    @Value("${server.defaultImage}")
    private String defaultServerImage;

    @Autowired
    public ApplicationSetup(PathProvider paths, DockerImageBuilder dockerImageBuilder, DockerImageRepository imageRepository,
                            BrokerTypeRepository brokerTypeRepository, ApplicationStatus status,
                            BrokerSeeder brokerSeeder, SchemaViewSeeder viewSeeder, MigrationRunner migrationRunner) {
        this.paths = paths;
        this.dockerImageBuilder = dockerImageBuilder;
        this.imageRepository = imageRepository;
        this.brokerTypeRepository = brokerTypeRepository;
        this.brokerSeeder = brokerSeeder;
        this.viewSeeder = viewSeeder;
        this.migrationRunner = migrationRunner;
        this.logger = LogManager.getLogger(ApplicationSetup.class);
        this.status = status;
    }

    public void start() throws LockException, IOException {
        if (!status.getSetupStatus().getCurrentStep().equals(ApplicationSetupStatus.Step.IDLE)) {
            throw new LockException("application setup is already running");
        }
        createDirectories();
        prepareDockerImages();
        brokerSeeder.seedBrokers();
        viewSeeder.seedViews();
        migrationRunner.runMigrations();
        status.setRunning();
    }

    private void createDirectories() throws IOException {
        Files.createDirectories(paths.local().brokers());
        Files.createDirectories(paths.local().games());
    }

    private void prepareDockerImages() {
        pullImage(defaultServerImage);
        buildBrokerImages();
    }

    private void pullImage(String tag) {
        if (!imageRepository.exists(tag)) {
            try {
                logger.info(String.format("pulling image '%s' ...", tag));
                imageRepository.pull(tag);
                logger.info(String.format("image '%s' pulled successfully", tag));
            }
            catch (InterruptedException|DockerException e) {
                logger.error(String.format("an error occurred while pulling image '%s': %s", tag, e.getMessage()), e);
                status.setInconsistent(e);
            }
        }
    }

    // TODO : create component - BrokerInitializer
    private void buildBrokerImages() {
        for (BrokerType type : brokerTypeRepository.findAll().values()) {
            if (shouldBuild(type)) {
                try {
                    logger.info(String.format("building image for broker '%s' ...", type.getName()));
                    dockerImageBuilder.buildImage(
                        getDockerfilePath(type),
                        getDefaultImageTags(type));
                    logger.info(String.format("image build successful for broker '%s'", type.getName()));
                } catch (DockerException e) {
                    logger.error(String.format("could not build image for broker '%s'", type.getName()));
                    status.setInconsistent(e);
                }
            }
        }
    }

    private String getDockerfilePath(BrokerType type) {
        return String.format("%s/Dockerfile", type.getPath());
    }

    private boolean shouldBuild(BrokerType type) {
        return type.isEnabled()
            && !imageRepository.exists(type.getImage())
            && Files.exists(Paths.get(getDockerfilePath(type)));
    }

    private Set<String> getDefaultImageTags(BrokerType type) {
        return Stream.of(type.getImage()).collect(Collectors.toSet());
    }

}
