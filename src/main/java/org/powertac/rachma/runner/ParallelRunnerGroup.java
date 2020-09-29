package org.powertac.rachma.runner;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

// TODO : move everything to SynchronousParallelRunnerGroup and remove this class
public class ParallelRunnerGroup implements Runner {

    private final Set<Runner> runners;
    private final AtomicBoolean stopSignalReceived = new AtomicBoolean(false);
    private Exception firstThrownException;

    public ParallelRunnerGroup(Set<Runner> runners) {
        this.runners = runners;
    }

    @Override
    public void run() throws Exception {
        for (Runner runner : runners) {
            runDetached(runner);
        }
        if (null != firstThrownException) {
            throw firstThrownException;
        }
    }

    @Override
    public void stop() {
        if (!stopSignalReceived.get()) {
            stopSignalReceived.set(true);
            stopAllRunners();
        }
    }

    private void stopAllRunners() {
        for (Runner runner : runners) {
            runner.stop();
        }
    }

    private void runDetached(Runner runner) {
        new Thread(() -> {
            try {
                runner.run();
            }
            catch (Exception e) {
                deferException(e);
                stop();
            }
        }).start();
    }

    private void deferException(Exception exception) {
        if (null == firstThrownException) {
            firstThrownException = exception;
        }
    }
}
