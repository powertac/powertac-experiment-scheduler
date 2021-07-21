package org.powertac.rachma.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class JpaConfig {

    private final static String defaultTimeZone = "Europe/Berlin";

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String baseUrl;

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
