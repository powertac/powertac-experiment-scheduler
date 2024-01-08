package org.powertac.orchestrator.docker;

import org.powertac.orchestrator.docker.exception.ContainerException;
import org.powertac.orchestrator.exec.PersistentTaskRepository;
import org.powertac.orchestrator.exec.TaskExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ContainerTaskExecutor implements TaskExecutor<ContainerTask> {

    private final PersistentTaskRepository taskRepository;
    private final DockerContainerController controller;
    private final Map<Class<? extends ContainerTask>, ContainerCreator<? extends ContainerTask>> creators = new HashMap<>();
    private final AtomicBoolean busy = new AtomicBoolean(false);

    public ContainerTaskExecutor(PersistentTaskRepository taskRepository, DockerContainerController controller) {
        this.taskRepository = taskRepository;
        this.controller = controller;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void exec(ContainerTask task) {
        if (accepts(task)) {
            try {
                busy.set(true);
                task.setStart(Instant.now());
                taskRepository.save(task);
                ContainerCreator<? extends ContainerTask> creator = creators.get(task.getClass());
                Method creationMethod = resolveCreationMethod(task.getClass(), (Class<? extends ContainerCreator<? extends ContainerTask>>) creator.getClass());
                DockerContainer container = (DockerContainer) creationMethod.invoke(creator, task);
                DockerContainerExitState exit = controller.run(container);
                task.setFailed(exit.isErrorState());
            } catch (NoSuchMethodException|IllegalAccessException|InvocationTargetException|ContainerException e) {
                throw new RuntimeException(e);
            } finally {
                task.setEnd(Instant.now());
                taskRepository.save(task);
                busy.set(false);
            }
        } else {
            throw new RuntimeException(ContainerTaskExecutor.class + " does not accept tasks of type + " + task.getClass());
        }
    }

    @Override
    public boolean accepts(ContainerTask task) {
        return creators.containsKey(task.getClass());
    }

    @Override
    public boolean hasCapacity() {
        return busy.get();
    }

    public void setCreator(Class<? extends ContainerTask> taskType, ContainerCreator<? extends ContainerTask> creator) {
        creators.put(taskType, creator);
    }

    private Method resolveCreationMethod(Class<? extends ContainerTask> taskType, Class<? extends ContainerCreator<? extends ContainerTask>> creatorType) throws NoSuchMethodException {
        Class<?> currentType = taskType;
        do {
            try {
                return creatorType.getMethod("createFor", currentType);
            } catch (NoSuchMethodException e) {
                if (currentType.equals(taskType.getSuperclass())) {
                    throw new NoSuchMethodException("failed to analyze " + creatorType);
                }
                currentType = taskType.getSuperclass();
            }
        } while (currentType != null && ContainerTask.class.isAssignableFrom(currentType));
        throw new NoSuchMethodException("no creation method found for " + taskType + " on class " + creatorType);
    }

}
