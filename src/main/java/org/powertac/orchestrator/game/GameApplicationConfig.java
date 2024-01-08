package org.powertac.orchestrator.game;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameApplicationConfig implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Bean
    public GameSchedule schedule() {
        DelegatingGameSchedule schedule = new DelegatingGameSchedule();
        schedule.register(context.getBean(FifoGameSchedule.class));
        return schedule;
    }

}
