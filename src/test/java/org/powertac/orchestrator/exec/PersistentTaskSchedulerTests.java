package org.powertac.orchestrator.exec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PersistentTaskSchedulerTests {

    @Test
    void addWithEmptySchedule() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);
        Task task = new ExecMocks.SampleTask();
        scheduler.add(task);
        Assertions.assertEquals(0, task.getPriority());
    }

    @Test
    void addWithExistingTask() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);
        priorityManager.setPriority(taskWithPriority(0), 0);
        Task task = new ExecMocks.SampleTask();
        scheduler.add(task);
        Assertions.assertEquals(-1 * PersistentTaskScheduler.defaultShiftDistance, task.getPriority());
    }

    @Test
    void injectBeforeTaskWithoutPriority() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);
        priorityManager.setPriority(taskWithPriority(-100), -100);
        Task reference = new ExecMocks.SampleTask();
        Task task = new ExecMocks.SampleTask();
        scheduler.inject(task).before(reference);
        Assertions.assertEquals(-100 - PersistentTaskScheduler.defaultShiftDistance, task.getPriority());
    }

    @Test
    void injectBeforeHighestPriorityTask() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);
        Task highestPriority = taskWithPriority(100);
        priorityManager.setPriority(highestPriority, 100);
        Task task = new ExecMocks.SampleTask();
        scheduler.inject(task).before(highestPriority);
        Assertions.assertEquals(100 + PersistentTaskScheduler.defaultShiftDistance, task.getPriority());
    }

    @Test
    void injectBeforeTaskThatHasPrecedingTask() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);

        Task reference = taskWithPriority(100);
        priorityManager.setPriority(reference, 100);
        priorityManager.setPriority(taskWithPriority(110), 110);
        Task task = new ExecMocks.SampleTask();

        scheduler.inject(task).before(reference);

        Assertions.assertEquals(
            100 + (int) Math.floor(((double) 110 - 100) / 2),
            task.getPriority());
    }

    @Test
    void injectBeforeWithShift() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);

        Task reference = taskWithPriority(100);
        priorityManager.setPriority(reference, 100);
        Task highestPriority = taskWithPriority(101);
        priorityManager.setPriority(highestPriority, 101);
        Task task = new ExecMocks.SampleTask();

        scheduler.inject(task).before(reference);

        Assertions.assertEquals(100, reference.getPriority());
        Assertions.assertEquals(101 + PersistentTaskScheduler.defaultShiftDistance, highestPriority.getPriority());
        Assertions.assertEquals(100 + (int) Math.floor((double) PersistentTaskScheduler.defaultShiftDistance / 2), task.getPriority());
    }

    @Test
    void injectAfterTaskWithNoPriorityThrowsIllegalArgumentException() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);

        Task reference = new ExecMocks.SampleTask();
        Task task = new ExecMocks.SampleTask();

        Assertions.assertThrows(IllegalArgumentException.class, () -> scheduler.inject(task).after(reference));
    }

    @Test
    void injectAfterLowestPriorityTask() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);

        Task lowestPriority = taskWithPriority(-50);
        priorityManager.setPriority(lowestPriority, -50);
        Task task = new ExecMocks.SampleTask();

        scheduler.inject(task).after(lowestPriority);

        Assertions.assertEquals(-50 - PersistentTaskScheduler.defaultShiftDistance, task.getPriority());
    }

    @Test
    void injectAfterTaskThatHasSucceedingTask() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);

        Task reference = taskWithPriority(-72);
        priorityManager.setPriority(reference, -72);
        priorityManager.setPriority(taskWithPriority(-93), -93);
        Task task = new ExecMocks.SampleTask();

        scheduler.inject(task).after(reference);

        Assertions.assertEquals(-72 - (int) Math.floor((double) Math.abs(-93 - (-72)) / 2), task.getPriority());
    }

    @Test
    void injectAfterWithShift() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);

        Task reference = taskWithPriority(-1782);
        priorityManager.setPriority(reference, -1782);
        Task lowestPriority = taskWithPriority(-1783);
        priorityManager.setPriority(lowestPriority, -1783);
        Task task = new ExecMocks.SampleTask();

        scheduler.inject(task).after(reference);

        Assertions.assertEquals(-1782, reference.getPriority());
        Assertions.assertEquals(-1783 - PersistentTaskScheduler.defaultShiftDistance, lowestPriority.getPriority());
        Assertions.assertEquals(-1782 - (int) Math.floor((double) PersistentTaskScheduler.defaultShiftDistance / 2), task.getPriority());
    }

    @Test
    void addingChain() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);

        Task A = new ExecMocks.SampleTask();
        scheduler.add(A);
        Assertions.assertEquals(0, A.getPriority());

        Task B = new ExecMocks.SampleTask();
        scheduler.add(B);
        Assertions.assertEquals(-1 * PersistentTaskScheduler.defaultShiftDistance, B.getPriority());

        Task C = new ExecMocks.SampleTask();
        scheduler.add(C);
        Assertions.assertEquals(-2 * PersistentTaskScheduler.defaultShiftDistance, C.getPriority());
    }

    @Test
    void addalot() {
        TaskPriorityManager priorityManager = new TransientPriorityManager();
        TaskScheduler scheduler = new PersistentTaskScheduler(priorityManager);
        for (int i = 0; i < 10000; i++) {
            Assertions.assertDoesNotThrow(() -> scheduler.add(new ExecMocks.SampleTask()));
        }
    }

    private Task taskWithPriority(Integer priority) {
        return ExecMocks.SampleTask.builder().priority(priority).build();
    }

}
