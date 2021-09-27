package org.powertac.rachma.persistence;

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

    @Value("${persistence.legacy.enable-mongo}")
    private boolean mongoDbEnabled;

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Bean
    public MigrationRunner migrationRunner() {
        MigrationRunner runner = context.getBean(MigrationRunnerImpl.class);
        if (mongoDbEnabled) {
            runner.registerMigration(context.getBean(NewGameModelMigration.class));
        }
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
        StringBuilder builder = new StringBuilder();
        builder.append(baseUrl);
        builder.append("?createDatabaseIfNotExist=true");
        builder.append("&serverTimezone=").append(defaultTimeZone); // time zone
        builder.append("&useLegacyDatetimeCode=false");
        return builder.toString();
    }

}
