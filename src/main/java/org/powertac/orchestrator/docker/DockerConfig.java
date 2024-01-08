package org.powertac.orchestrator.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class DockerConfig {

    @Bean
    public DockerClient docker() throws IOException {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("unix:///var/run/docker.sock")
            .build();
        DockerHttpClient http = new ZerodepDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();
        return DockerClientImpl.getInstance(config, http);
    }

}
