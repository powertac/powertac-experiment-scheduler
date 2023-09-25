package org.powertac.rachma.broker;

import org.powertac.rachma.docker.DockerImageRepository;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class BrokerValidatorImpl implements BrokerValidator {

    private static Pattern nameAndVersionPattern = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9_.-]");

    private final BrokerRepository brokerRepository;
    private final DockerImageRepository imageRepository;

    public BrokerValidatorImpl(BrokerRepository brokerRepository, DockerImageRepository imageRepository) {
        this.brokerRepository = brokerRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public void validate(Broker broker) throws BrokerValidationException {
        validateNameOrVersion(broker.getName());
        validateNameOrVersion(broker.getVersion());
        if (broker.isEnabled()) {
            validateImageExistence(broker.getImageTag());
        }
    }

    private void validateNameOrVersion(String name) throws BrokerValidationException {
        if (!nameAndVersionPattern.matcher(name).matches()) {
            throw new BrokerValidationException(
                "names and versions must only consist of letters (upper or lowercase), numbers, underscores(_), dots(.) or hyphens(-) and start with a letter or a number");
        }
    }

    private void validateImageExistence(String imageTag) throws BrokerValidationException {
        if (!imageRepository.exists(imageTag)) {
            throw new BrokerValidationException(String.format("broker image with tag '%s' does not exist", imageTag));
        }
    }

}
