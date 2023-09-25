package org.powertac.rachma.exec;

public interface TaskExecutor<T extends Task> {

    void exec(T task);
    boolean accepts(T task);

}
