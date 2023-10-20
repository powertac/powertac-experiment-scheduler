package org.powertac.rachma.exec;

import org.springframework.core.ResolvableType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class DelegatingTaskExecutor implements TaskExecutor<Task> {

    private final Set<TaskExecutor<?>> executors = new HashSet<>();
    private final AtomicBoolean busy = new AtomicBoolean(false);

    @Override
    public synchronized void exec(Task task) {
        try {
            busy.set(true);
            Optional<TaskExecutor<?>> executor = getExecutor(task);
            if (executor.isPresent()) {
                delegate("exec", executor.get(), task);
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("unable to delegate", e);
        } finally {
            busy.set(false);
        }
    }

    @Override
    public synchronized boolean accepts(Task task) {
        try {
            return getExecutor(task).isPresent();
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("unable to delegate", e);
        }
    }

    @Override
    public boolean hasCapacity() {
        return !busy.get();
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
