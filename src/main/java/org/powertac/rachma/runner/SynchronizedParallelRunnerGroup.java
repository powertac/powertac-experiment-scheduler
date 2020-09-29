package org.powertac.rachma.runner;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SynchronizedParallelRunnerGroup extends ParallelRunnerGroup implements Runner {

    private final Set<Runner> runners;
    private final Set<Runner> completedRunners = new HashSet<>();
    private final int gracePeriodMilliseconds;
    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private Exception firstThrownException;

    public SynchronizedParallelRunnerGroup(Set<Runner> runners, int gracePeriodMilliseconds) {
        super(runners);
        this.runners = runners;
        this.gracePeriodMilliseconds = gracePeriodMilliseconds;
    }

    @Override
    public void run() throws Exception {
        Set<Thread> runnerThreads = new HashSet<>();
        for (Runner runner : runners) {
            Thread runnerThread = createRunnerThread(runner);
            runnerThreads.add(runnerThread);
            runnerThread.start();
        }
        for (Thread runnerThread : runnerThreads) {
            runnerThread.join();
        }
        if (null != firstThrownException) {
            throw firstThrownException;
        }
    }

    private Thread createRunnerThread(Runner runner) {
        return new Thread(() -> {
            try {
                runner.run();
                setCompleted(runner);
                if (!shutdownInitiated.get()) {
                    shutdownInitiated.set(true);
                    shutdown();
                }
            }
            catch (Exception e) {
                deferException(e);
                stop();
            }
        });
    }

    private void setCompleted(Runner runner) {
        completedRunners.add(runner);
    }

    private void shutdown() {
        try {
            if (allRunnersCompleted()) {
                return;
            }
            awaitGracePeriod();
            if (!allRunnersCompleted()) {
                stop();
            }
        }
        catch (InterruptedException e) {
            stop();
        }
    }

    private void deferException(Exception exception) {
        // only the inital exception is kept and is considered the cause of any shutdown (stop)
        if (null == firstThrownException) {
            firstThrownException = exception;
        }
    }

    private void awaitGracePeriod() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(gracePeriodMilliseconds);
    }

    private boolean allRunnersCompleted() {
        return runners.size() == completedRunners.size();
    }

}
