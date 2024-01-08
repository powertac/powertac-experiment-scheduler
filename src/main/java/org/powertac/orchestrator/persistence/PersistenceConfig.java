package org.powertac.orchestrator.persistence;

import org.powertac.orchestrator.persistence.migration.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class PersistenceConfig implements ApplicationContextAware {

    private final static String defaultTimeZone = "Europe/Berlin";

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String baseUrl;

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Bean
    public MigrationRunner migrationRunner() {
        MigrationStatusRepository statusRepository = context.getBean(MigrationStatusRepository.class);
        MigrationRunner runner = new MigrationRunnerImpl(statusRepository);
        return runner;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setUrl(getDatasourceUrl());
        return dataSource;
    }

    private String getDatasourceUrl() {
        // TODO : is this still required
        return baseUrl +
            "?createDatabaseIfNotExist=true" +
            "&serverTimezone=" + defaultTimeZone +
            "&useLegacyDatetimeCode=false";
    }

}
