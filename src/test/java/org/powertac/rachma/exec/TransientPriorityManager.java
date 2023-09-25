package org.powertac.rachma.exec;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TransientPriorityManager implements TaskPriorityManager {

    private Map<Integer, Task> taskMap = new HashMap<>();

    @Override
    public Optional<Task> getLowestPriorityTask() {
        return taskMap.keySet().stream().min(Comparator.comparingInt(a -> a)).map(taskMap::get);
    }

    @Override
    public Optional<Task> getHighestPriorityTask() {
        return taskMap.keySet().stream().max(Comparator.comparingInt(a -> a)).map(taskMap::get);
    }

    @Override
    public void shiftPrioritiesUp(int exclusiveLowerBoundary, int distance) {
        Map<Integer, Task> shiftedMap = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : taskMap.entrySet()) {
            int newPriority = entry.getKey() > exclusiveLowerBoundary
                ? entry.getKey() + distance
                : entry.getKey();
            shiftedMap.put(newPriority, entry.getValue());
            ((PersistentTask) entry.getValue()).setPriority(newPriority);
        }
        taskMap = shiftedMap;
    }

    @Override
    public void shiftPrioritiesDown(int exclusiveLowerBoundary, int distance) {
        Map<Integer, Task> shiftedMap = new HashMap<>();
        for (Map.Entry<Integer, Task> entry : taskMap.entrySet()) {
            int newPriority = entry.getKey() < exclusiveLowerBoundary
                ? entry.getKey() - distance
                : entry.getKey();
            shiftedMap.put(newPriority, entry.getValue());
            ((PersistentTask) entry.getValue()).setPriority(newPriority);
        }
        taskMap = shiftedMap;
    }

    @Override
    public Integer getNextLowerPriority(int priority) {
        return taskMap.keySet().stream()
            .filter(i -> i < priority)
            .max(Comparator.naturalOrder())
            .orElse(null);
    }

    @Override
    public Integer getNextHigherPriority(int priority) {
        return taskMap.keySet().stream()
            .filter(i -> i > priority)
            .min(Comparator.naturalOrder())
            .orElse(null);
    }

    @Override
    public void setPriority(Task task, Integer priority) {
        if (taskMap.containsKey(priority)) {
            throw new IllegalArgumentException("a task with priority=" + priority + " already exists");
        }
        if (taskMap.containsValue(task)) {
            taskMap.remove(task);
        }
        taskMap.put(priority, task);
        ((PersistentTask) task).setPriority(priority);
    }

}
