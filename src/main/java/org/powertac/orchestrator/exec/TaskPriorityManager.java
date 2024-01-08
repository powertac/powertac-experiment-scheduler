package org.powertac.orchestrator.exec;

import java.util.Optional;

public interface TaskPriorityManager {

    Optional<Task> getLowestPriorityTask();
    Optional<Task> getHighestPriorityTask();

    void shiftPrioritiesUp(int exclusiveLowerBoundary, int distance);
    void shiftPrioritiesDown(int exclusiveLowerBoundary, int distance);

    Integer getNextLowerPriority(int priority);
    Integer getNextHigherPriority(int priority);

    void setPriority(Task task, Integer priority);

}
