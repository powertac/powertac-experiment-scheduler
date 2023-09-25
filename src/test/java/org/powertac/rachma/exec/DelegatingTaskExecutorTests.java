package org.powertac.rachma.exec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DelegatingTaskExecutorTests {

    @Test
    public void delegatesWithOneDelegate() {
        DelegatingTaskExecutor executor = new DelegatingTaskExecutor();
        ExecMocks.SampleTaskExecutor delegate = new ExecMocks.SampleTaskExecutor();
        executor.addExecutor(delegate);
        Task task = new ExecMocks.SampleTask();
        Assertions.assertDoesNotThrow(() -> executor.exec(task));
        Assertions.assertEquals(1, delegate.getExecCallCount());
    }

    @Test
    public void delegatesWithTwoDelegates() {
        DelegatingTaskExecutor executor = new DelegatingTaskExecutor();
        ExecMocks.SampleTaskExecutor delegate = new ExecMocks.SampleTaskExecutor();
        executor.addExecutor(delegate);
        ExecMocks.AnotherSampleTaskExecutor anotherDelegate = new ExecMocks.AnotherSampleTaskExecutor();
        executor.addExecutor(anotherDelegate);
        Task task = new ExecMocks.AnotherSampleTask();
        Assertions.assertDoesNotThrow(() -> executor.exec(task));
        Assertions.assertEquals(0, delegate.getExecCallCount());
        Assertions.assertEquals(1, anotherDelegate.getExecCallCount());
    }

    @Test
    public void delegatesToMoreGenericExecutor() {
        DelegatingTaskExecutor executor = new DelegatingTaskExecutor();
        ExecMocks.SampleTaskExecutor delegate = new ExecMocks.SampleTaskExecutor();
        executor.addExecutor(delegate);
        Task task = new ExecMocks.DerivedSampleTask();
        Assertions.assertDoesNotThrow(() -> executor.exec(task));
        Assertions.assertEquals(1, delegate.getExecCallCount());
    }

    @Test
    public void doesNotAcceptIfNoMatchingExecutorIsPresent() {
        DelegatingTaskExecutor executor = new DelegatingTaskExecutor();
        ExecMocks.SampleTaskExecutor delegate = new ExecMocks.SampleTaskExecutor();
        executor.addExecutor(delegate);
        Task task = new ExecMocks.AnotherSampleTask();
        Assertions.assertFalse(executor.accepts(task));
    }

}
