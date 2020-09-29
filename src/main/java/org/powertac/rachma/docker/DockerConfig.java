package org.powertac.rachma.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Bean
    public DockerClient docker() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        return DockerClientBuilder.getInstance(config).build();
    }

}
