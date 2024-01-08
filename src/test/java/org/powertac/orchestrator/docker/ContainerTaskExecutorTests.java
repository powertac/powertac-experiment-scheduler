package org.powertac.orchestrator.docker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.powertac.orchestrator.docker.exception.ContainerException;
import org.powertac.orchestrator.exec.ExecMocks;
import org.powertac.orchestrator.exec.PersistentTaskRepository;

import java.time.Instant;

public class ContainerTaskExecutorTests {

    @Test
    public void setCreator() {
        PersistentTaskRepository taskRepository = Mockito.mock(PersistentTaskRepository.class);
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        ContainerTaskExecutor executor = new ContainerTaskExecutor(taskRepository, controller);
        Assertions.assertDoesNotThrow(() ->
            executor.setCreator(
                ContainerTask.class,
                Mockito.mock(ContainerCreator.class)));
    }

    @Test
    public void acceptsForExistingCreator() {
        PersistentTaskRepository taskRepository = Mockito.mock(PersistentTaskRepository.class);
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        ContainerTaskExecutor executor = new ContainerTaskExecutor(taskRepository, controller);
        ContainerCreator<ContainerTask> creator = Mockito.mock(ContainerCreator.class);
        executor.setCreator(ExecMocks.SampleContainerTask.class, creator);
        Assertions.assertTrue(executor.accepts(new ExecMocks.SampleContainerTask()));
    }

    @Test
    public void acceptsFailsForNonExistentCreator() {
        PersistentTaskRepository taskRepository = Mockito.mock(PersistentTaskRepository.class);
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        ContainerTaskExecutor executor = new ContainerTaskExecutor(taskRepository, controller);
        Assertions.assertFalse(executor.accepts(Mockito.mock(ContainerTask.class)));
    }

    @Test
    public void execWithMatchingCreator() throws ContainerException {
        PersistentTaskRepository taskRepository = Mockito.mock(PersistentTaskRepository.class);
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        Mockito.doReturn(new DockerContainerExitState(0, Instant.now().toString())).when(controller).run(Mockito.any(DockerContainer.class));
        ContainerTaskExecutor executor = new ContainerTaskExecutor(taskRepository, controller);
        ContainerCreator<ExecMocks.SampleContainerTask> creator = new ExecMocks.SampleContainerTaskCreator();
        ExecMocks.SampleContainerTask task = new ExecMocks.SampleContainerTask();
        executor.setCreator(ExecMocks.SampleContainerTask.class, creator);
        executor.exec(task);
        Mockito.verify(controller, Mockito.times(1)).run(Mockito.any(DockerContainer.class));
    }

    @Test
    public void execWithoutMatchingCreatorThrowsRuntimeException() {
        PersistentTaskRepository taskRepository = Mockito.mock(PersistentTaskRepository.class);
        DockerContainerController controller = Mockito.mock(DockerContainerController.class);
        ContainerTaskExecutor executor = new ContainerTaskExecutor(taskRepository, controller);
        ContainerCreator<ContainerTask> creator = Mockito.mock(ContainerCreator.class);
        executor.setCreator(ContainerTask.class, creator);
        Assertions.assertThrows(RuntimeException.class, () -> executor.exec(new ExecMocks.SampleContainerTask()));
    }

}
