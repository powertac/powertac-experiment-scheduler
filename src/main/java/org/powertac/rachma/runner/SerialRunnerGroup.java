package org.powertac.rachma.runner;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SerialRunnerGroup implements Runner {

    private final List<Runner> runners;
    private Runner currentTaskRunner;
    private final AtomicBoolean stopSignalReceived = new AtomicBoolean(false);

    public SerialRunnerGroup(List<Runner> taskRunners) {
        this.runners = taskRunners;
    }

    @Override
    public void run() throws Exception {
        for (Runner runner : runners) {
            if (shouldExecuteNextTask()) {
                currentTaskRunner = runner;
                runner.run();
            }
        }
    }

    @Override
    public void stop() {
        stopSignalReceived.set(true);
        if (null != currentTaskRunner) {
            currentTaskRunner.stop();
        }
    }

    private boolean shouldExecuteNextTask() {
        return !stopSignalReceived.get();
    }

}
