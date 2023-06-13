package org.powertac.rachma.docker;

import org.powertac.rachma.docker.exception.ContainerException;
import org.powertac.rachma.exec.TaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class ContainerTaskExecutor implements TaskExecutor<ContainerTask> {

    private final DockerContainerController controller;
    private final Map<Class<? extends ContainerTask>, ContainerCreator<? extends ContainerTask>> creators = new HashMap<>();

    public ContainerTaskExecutor(DockerContainerController controller) {
        this.controller = controller;
    }

    @Override
    public void exec(ContainerTask task) {
        if (accepts(task)) {
            try {
                // TODO : add lifecycle management for tasks
                ContainerCreator<? extends ContainerTask> creator = creators.get(task.getClass());
                Method creationMethod = resolveCreationMethod(task.getClass(), (Class<? extends ContainerCreator<? extends ContainerTask>>) creator.getClass());
                DockerContainer container = (DockerContainer) creationMethod.invoke(creator, task);
                controller.run(container);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (ContainerException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("doesn't accept"); // FIXME : create specific Exception type
        }
    }

    @Override
    public boolean accepts(ContainerTask task) {
        return creators.containsKey(task.getClass());
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
