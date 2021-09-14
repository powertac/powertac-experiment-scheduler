package org.powertac.rachma.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.DockerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiDrivenDockerImageRepository implements DockerImageRepository {

    private final DockerClient dockerClient;

    @Autowired
    public ApiDrivenDockerImageRepository(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void pull(String tag) throws DockerException, InterruptedException {
        dockerClient.pullImageCmd(tag)
            .exec(new PullImageResultCallback())
            .awaitCompletion();
    }

    @Override
    public boolean exists(String tag) throws DockerException {
        try {
            dockerClient.inspectImageCmd(tag).exec();
            return true;
        }
        catch (com.github.dockerjava.api.exception.NotFoundException e) {
            return false;
        }
    }

}
