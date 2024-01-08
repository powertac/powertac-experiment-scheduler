package org.powertac.orchestrator.exec;

import org.powertac.orchestrator.analysis.AnalyzerContainerCreator;
import org.powertac.orchestrator.analysis.AnalyzerTask;
import org.powertac.orchestrator.docker.ContainerTask;
import org.powertac.orchestrator.docker.ContainerTaskExecutor;
import org.powertac.orchestrator.docker.DockerContainerController;
import org.powertac.orchestrator.game.file.GameFileExportTaskExecutor;
import org.powertac.orchestrator.logprocessor.LogProcessorContainerCreator;
import org.powertac.orchestrator.logprocessor.LogProcessorTask;
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
        executor.setCreator(LogProcessorTask.class, context.getBean(LogProcessorContainerCreator.class));
        executor.setCreator(AnalyzerTask.class, context.getBean(AnalyzerContainerCreator.class));
        return executor;
    }

}
