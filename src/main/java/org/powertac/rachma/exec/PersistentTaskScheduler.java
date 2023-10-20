package org.powertac.rachma.exec;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersistentTaskScheduler implements TaskScheduler {

    public final static int defaultShiftDistance = 100;

    private final TaskPriorityManager priorityManager;

    public PersistentTaskScheduler(TaskPriorityManager priorityManager) {
        this.priorityManager = priorityManager;
    }

    @Override
    public synchronized void add(Task task) {
        Optional<Task> lowestPriorityTask = priorityManager.getLowestPriorityTask();
        if (lowestPriorityTask.isPresent()) {
            priorityManager.setPriority(task, lowestPriorityTask.get().getPriority() - defaultShiftDistance);
        } else {
            priorityManager.setPriority(task, 0);
        }
        // TODO : explicitly save task (no updates within priority manager!)
    }

    @Override
    public SchedulingOperation inject(Task task) {
        return new SchedulingOperationImpl(this, task);
    }

    @Override
    public synchronized Optional<Task> next() {
        return priorityManager.getHighestPriorityTask();
    }

    // FIXME : might make sense to fold insertBefore & insertAfter; differences are subtle
    private synchronized void insertAfter(Task task, Task reference) {
        if (null == reference.getPriority()) {
            throw new IllegalArgumentException("cannot insert task after task with priority=null");
        }
        Integer nextLowerPriority = priorityManager.getNextLowerPriority(reference.getPriority());
        if (null == nextLowerPriority) {
            priorityManager.setPriority(task, reference.getPriority() - defaultShiftDistance);
        } else {
            int distance = (int) Math.floor((double) Math.abs(reference.getPriority() - nextLowerPriority) / 2);
            if (0 == distance) {
                priorityManager.shiftPrioritiesDown(reference.getPriority(), defaultShiftDistance);
                distance = (int) Math.floor((double) defaultShiftDistance / 2);
            }
            priorityManager.setPriority(task, reference.getPriority() - distance);
        }
    }

    private synchronized void insertBefore(Task task, Task reference) {
        if (null == reference.getPriority()) {
            add(task); // add task to very beginning; if task with null priority is added, it will be added after by default
            return;
        }
        Integer nextHighestPriority = priorityManager.getNextHigherPriority(reference.getPriority());
        if (null == nextHighestPriority) {
            priorityManager.setPriority(task, reference.getPriority() + defaultShiftDistance);
        } else {
            int distance = (int) Math.floor((double) Math.abs(nextHighestPriority - reference.getPriority()) / 2);
            if (0 == distance) {
                priorityManager.shiftPrioritiesUp(reference.getPriority(), defaultShiftDistance);
                distance = (int) Math.floor((double) defaultShiftDistance / 2);
            }
            priorityManager.setPriority(task, reference.getPriority() + distance);
        }
    }

    @AllArgsConstructor
    private static class SchedulingOperationImpl implements SchedulingOperation {

        private final PersistentTaskScheduler scheduler;
        private final Task task;

        @Override
        public void before(Task reference) {
            scheduler.insertBefore(task, reference);
        }

        @Override
        public void after(Task reference) {
            scheduler.insertAfter(task, reference);
        }

    }

}
