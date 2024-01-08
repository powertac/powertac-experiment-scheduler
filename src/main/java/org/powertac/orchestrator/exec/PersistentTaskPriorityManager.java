package org.powertac.orchestrator.exec;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersistentTaskPriorityManager implements TaskPriorityManager {

    private final PersistentTaskRepository repository;

    public PersistentTaskPriorityManager(PersistentTaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Task> getLowestPriorityTask() {
        return repository.findFirstByStartIsNullOrderByPriorityAsc().map(p ->p);
    }

    @Override
    public Optional<Task> getHighestPriorityTask() {
        return repository.findFirstByStartIsNullOrderByPriorityDesc().map(p ->p);
    }

    @Override
    public void shiftPrioritiesUp(int exclusiveLowerBoundary, int distance) {
        repository.shiftPrioritiesUp(exclusiveLowerBoundary, distance);
    }

    @Override
    public void shiftPrioritiesDown(int exclusiveUpperBoundary, int distance) {
        repository.shiftPrioritiesDown(exclusiveUpperBoundary, distance);
    }

    @Override
    public Integer getNextLowerPriority(int priority) {
        return repository.getNextLowerPriority(priority);
    }

    @Override
    public Integer getNextHigherPriority(int priority) {
        return repository.getNextHigherPriority(priority);
    }

    @Override
    public void setPriority(Task task, Integer priority) {
        PersistentTask persistentTask = (PersistentTask) task;
        persistentTask.setPriority(priority);
        repository.save(persistentTask);
    }

}
