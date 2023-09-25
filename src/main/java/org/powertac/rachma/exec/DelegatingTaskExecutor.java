package org.powertac.rachma.exec;

import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DelegatingTaskExecutor implements TaskExecutor<Task> {

    private final Set<TaskExecutor<?>> executors = new HashSet<>();

    @Override
    public void exec(Task task) {
        try {
            Optional<TaskExecutor<?>> executor = getExecutor(task);
            if (executor.isPresent()) {
                delegate("exec", executor.get(), task);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("unable to delegate", e);
        }
    }

    @Override
    public boolean accepts(Task task) {
        try {
            return getExecutor(task).isPresent();
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("unable to delegate", e);
        }
    }

    private Optional<TaskExecutor<?>> getExecutor(Task task) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        for (TaskExecutor<?> executor : executors) {
            if (delegateAccepts(executor, task)) {
                return Optional.of(executor);
            }
        }
        return Optional.empty();
    }

    public void addExecutor(TaskExecutor<?> executor) {
        executors.add(executor);
    }

    private boolean delegateAccepts(TaskExecutor<?> executor, Task task) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return getMostSpecificType(executor).isAssignableFrom(task.getClass())
            && (boolean) delegate("accepts", executor, task);
    }

    private ResolvableType getMostSpecificType(TaskExecutor<?> executor) {
        return ResolvableType.forClass(executor.getClass())
            .as(TaskExecutor.class)
            .getGeneric(0);
    }

    private Object delegate(String methodName, TaskExecutor<?> executor, Task task) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ResolvableType type = getMostSpecificType(executor);
        Method method = executor.getClass().getMethod(methodName, type.getRawClass());
        return method.invoke(executor, task);
    }

}
