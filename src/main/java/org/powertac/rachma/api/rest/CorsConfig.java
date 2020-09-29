package org.powertac.rachma.api.rest;

import org.powertac.rachma.api.AllowedOriginsProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    private final AllowedOriginsProvider originsProvider;

    public CorsConfig(AllowedOriginsProvider originsProvider) {
        this.originsProvider = originsProvider;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedMethods("GET", "POST")
                    .allowedOrigins(originsProvider.getAllowedOrigins());
            }
        };
    }

}
