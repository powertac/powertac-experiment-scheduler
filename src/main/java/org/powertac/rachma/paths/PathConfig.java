package org.powertac.rachma.paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathConfig {

    @Value("${directory.host.base}")
    private String hostBasePath;

    @Value("${directory.local.base}")
    private String localBasePath;

    @Bean
    public PathProvider pathProvider() {
        return new DefaultPathProvider(hostBasePath, localBasePath);
    }

}
