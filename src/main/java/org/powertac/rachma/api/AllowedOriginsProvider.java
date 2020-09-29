package org.powertac.rachma.api;

import org.powertac.rachma.RachmaApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

// TODO : replace with some sort of service discovery

@Component
public class AllowedOriginsProvider {

    @Value("${application.mode}")
    private String applicationMode;

    public String[] getAllowedOrigins() {

        Set<String> allowedOrigins = new HashSet<>();

        if (RachmaApplication.Mode.DEVELOPMENT.equals(RachmaApplication.Mode.from(applicationMode))) {
            allowedOrigins.add("http://localhost:9000");
        }

        String serviceManagerUri = System.getenv("SERVICE_MANAGER_URI");

        if (null != serviceManagerUri) {
            allowedOrigins.add(serviceManagerUri);
        }

        return allowedOrigins.toArray(new String[0]);
    }

}
