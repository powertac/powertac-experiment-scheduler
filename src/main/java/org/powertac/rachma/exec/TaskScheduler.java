package org.powertac.rachma.exec;

import java.util.Optional;

public interface TaskScheduler {

    void add(Task task);
    SchedulingOperation inject(Task task);
    Optional<Task> next();

    interface SchedulingOperation {
        void before(Task task);
        void after(Task task);
    }

}
