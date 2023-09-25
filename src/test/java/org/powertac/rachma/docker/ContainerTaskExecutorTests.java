package org.powertac.rachma.docker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.rachma.docker.exception.ContainerException;
import org.powertac.rachma.exec.ExecMocks;

public class ContainerTaskExecutorTests {

    @Test
    public void setCreator() {
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        ContainerTaskExecutor executor = new ContainerTaskExecutor(controller);
        Assertions.assertDoesNotThrow(() ->
            executor.setCreator(
                ContainerTask.class,
                Mockito.mock(ContainerCreator.class)));
    }

    @Test
    public void acceptsForExistingCreator() {
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        ContainerTaskExecutor executor = new ContainerTaskExecutor(controller);
        ContainerCreator<ContainerTask> creator = Mockito.mock(ContainerCreator.class);
        executor.setCreator(ExecMocks.SampleContainerTask.class, creator);
        Assertions.assertTrue(executor.accepts(new ExecMocks.SampleContainerTask()));
    }

    @Test
    public void acceptsFailsForNonExistentCreator() {
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        ContainerTaskExecutor executor = new ContainerTaskExecutor(controller);
        Assertions.assertFalse(executor.accepts(Mockito.mock(ContainerTask.class)));
    }

    @Test
    public void execWithMatchingCreator() throws ContainerException {
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        ContainerTaskExecutor executor = new ContainerTaskExecutor(controller);
        ContainerCreator<ExecMocks.SampleContainerTask> creator = new ExecMocks.SampleContainerTaskCreator();
        ExecMocks.SampleContainerTask task = new ExecMocks.SampleContainerTask();
        executor.setCreator(ExecMocks.SampleContainerTask.class, creator);
        executor.exec(task);
        Mockito.verify(controller, Mockito.times(1)).run(Mockito.any(DockerContainer.class));
    }

    @Test
    public void execWithoutMatchingCreatorThrowsRuntimeException() {
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        ContainerTaskExecutor executor = new ContainerTaskExecutor(controller);
        ContainerCreator<ContainerTask> creator = Mockito.mock(ContainerCreator.class);
        executor.setCreator(ContainerTask.class, creator);
        Assertions.assertThrows(RuntimeException.class, () -> executor.exec(new ExecMocks.SampleContainerTask()));
    }

}
