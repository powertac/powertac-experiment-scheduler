package org.powertac.rachma.exec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.powertac.rachma.docker.ContainerCreator;
import org.powertac.rachma.docker.ContainerTask;
import org.powertac.rachma.docker.DockerContainer;

public final class ExecMocks {

    @SuperBuilder
    @AllArgsConstructor
    public static class SampleTask extends PersistentTask {}

    public static class DerivedSampleTask extends SampleTask {}
    
    public static class SampleTaskExecutor implements TaskExecutor<SampleTask> {

        @Getter
        private int execCallCount;

        @Override
        public void exec(SampleTask task) {
            execCallCount++;
        }

        @Override
        public boolean accepts(SampleTask task) {
            return true;
        }

        @Override
        public boolean hasCapacity() {
            return true;
        }

    }

    public static class AnotherSampleTask extends PersistentTask {}

    public static class AnotherSampleTaskExecutor implements TaskExecutor<AnotherSampleTask> {

        @Getter
        private int execCallCount;

        @Override
        public void exec(AnotherSampleTask task) {
            execCallCount++;
        }

        @Override
        public boolean accepts(AnotherSampleTask task) {
            return task.getClass().equals(AnotherSampleTask.class);
        }

        @Override
        public boolean hasCapacity() {
            return true;
        }

    }

    public static class SampleContainerTask extends ContainerTask {}

    public static class SampleContainerTaskCreator implements ContainerCreator<SampleContainerTask> {

        @Override
        public DockerContainer createFor(SampleContainerTask entity) {
            return new DockerContainer("abcdefg", "Hans Peter");
        }

    }

}
