package org.powertac.rachma.application;

import com.github.dockerjava.api.exception.DockerException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.docker.image.DockerImageBuilder;
import org.powertac.rachma.docker.image.DockerImageRepository;
import org.powertac.rachma.broker.BrokerType;
import org.powertac.rachma.broker.BrokerTypeRepository;
import org.powertac.rachma.resource.WorkDirectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ApplicationSetup {

    private final DockerImageBuilder dockerImageBuilder;
    private final DockerImageRepository imageRepository;
    private final BrokerTypeRepository brokerTypeRepository;
    private final WorkDirectoryManager workDirectoryManager;
    private final Logger logger;
    private final ApplicationStatus status;

    @Value("${server.defaultImage}")
    private String defaultServerImage;

    @Value("${directory.local.brokers}")
    private String brokerDirectory;

    @Value("${broker.policy.descriptorFileName}")
    private String brokerDescriptorFilename;

    @Value("${broker.baseimage.java}")
    private String brokerBaseImageJava;

    @Autowired
    public ApplicationSetup(DockerImageBuilder dockerImageBuilder, DockerImageRepository imageRepository,
                            BrokerTypeRepository brokerTypeRepository, ApplicationStatus status,
                            WorkDirectoryManager workDirectoryManager) {
        this.dockerImageBuilder = dockerImageBuilder;
        this.imageRepository = imageRepository;
        this.brokerTypeRepository = brokerTypeRepository;
        this.workDirectoryManager = workDirectoryManager;
        this.logger = LogManager.getLogger(ApplicationSetup.class);
        this.status = status;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void start() throws LockException {
        if (!status.getSetupStatus().getCurrentStep().equals(ApplicationSetupStatus.Step.IDLE)) {
            throw new LockException("application setup is already running");
        }
        try {
            workDirectoryManager.createMainDirectoriesAndCopyResourceFiles();
            pullImage(defaultServerImage);
            pullImage(brokerBaseImageJava);
            buildBrokerImages();
        }
        catch (IOException e) {
            logger.error(e.getMessage());
            logger.error("error during setup lead to an inconsistent application state");
            status.setInconsistent(e);
        }
        finally {
            status.getSetupStatus().setCurrentStep(ApplicationSetupStatus.Step.IDLE);
        }
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

    private void buildBrokerImages() {
        for (BrokerType type : brokerTypeRepository.findAll().values()) {
            if (type.isEnabled() && !imageRepository.exists(type.getImage())) {
                try {
                    logger.info(String.format("building image for broker '%s' ...", type.getName()));
                    dockerImageBuilder.buildImage(
                        getDockerfilePath(type),
                        getDefaultImageTags(type));
                    logger.info(String.format("image build successful for broker '%s'", type.getName()));
                }
                catch (DockerException e) {
                    logger.error(String.format("could not build image for broker '%s'", type.getName()));
                    status.setInconsistent(e);
                }
            }
        }
    }

    private String getDockerfilePath(BrokerType type) {
        return String.format("%s%s/Dockerfile", brokerDirectory, type.getName().toLowerCase());
    }

    private Set<String> getDefaultImageTags(BrokerType type) {
        String latestTag = String.format("%s:latest", type.getName().toLowerCase());
        return Stream.of(latestTag).collect(Collectors.toSet());
    }

}
