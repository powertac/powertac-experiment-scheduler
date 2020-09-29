package org.powertac.rachma.docker.image;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.core.command.PullImageResultCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiDrivenDockerDockerImageRepository implements DockerImageRepository {

    private DockerClient dockerClient;

    @Autowired
    public ApiDrivenDockerDockerImageRepository(DockerClient dockerClient) {
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
