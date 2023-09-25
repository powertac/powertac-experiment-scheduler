package org.powertac.rachma.exec;

import org.springframework.core.ResolvableType;

public class TaskExecutorReflection {

    private final TaskExecutor<?> executor;

    public TaskExecutorReflection(TaskExecutor<?> executor) {
        this.executor = executor;
    }

    private ResolvableType getMostSpecificType() {
        return ResolvableType.forClass(executor.getClass())
            .as(TaskExecutor.class)
            .getGeneric(0);
    }

}
