package org.powertac.rachma.exec;

import org.powertac.rachma.docker.ContainerTask;
import org.powertac.rachma.docker.ContainerTaskExecutor;
import org.powertac.rachma.docker.DockerContainerController;
import org.powertac.rachma.game.file.GameFileExportTaskExecutor;
import org.powertac.rachma.logprocessor.LogProcessorContainerCreatorImpl;
import org.powertac.rachma.logprocessor.LogProcessorTask;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskConfiguration implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Bean
    public TaskExecutor<Task> taskExecutor() {
        DelegatingTaskExecutor executor = new DelegatingTaskExecutor();
        executor.addExecutor(containerTaskExecutor());
        executor.addExecutor(context.getBean(GameFileExportTaskExecutor.class));
        return executor;
    }

    @Bean
    public TaskExecutor<ContainerTask> containerTaskExecutor() {
        ContainerTaskExecutor executor = new ContainerTaskExecutor(
            context.getBean(PersistentTaskRepository.class),
            context.getBean(DockerContainerController.class));
        executor.setCreator(LogProcessorTask.class, context.getBean(LogProcessorContainerCreatorImpl.class));
        return executor;
    }

}
