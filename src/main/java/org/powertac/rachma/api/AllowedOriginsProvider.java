package org.powertac.rachma.api;

import org.powertac.rachma.ExperimentSchedulerApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO : keep for now; replace later with spring default config
//        (https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.cors)

@Component
public class AllowedOriginsProvider {

    @Value("${application.mode}")
    private String applicationMode;

    @Value("#{'${application.api.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    public String[] getAllowedOrigins() {
        Set<String> allowedOrigins = new HashSet<>();
        if (ExperimentSchedulerApplication.Mode.DEVELOPMENT.equals(ExperimentSchedulerApplication.Mode.from(applicationMode))) {
            allowedOrigins.add("http://localhost:9000");
        }
        String serviceManagerUri = System.getenv("SERVICE_MANAGER_URI");
        if (null != serviceManagerUri) {
            allowedOrigins.add(serviceManagerUri);
        }
        allowedOrigins.addAll(this.allowedOrigins);
        return allowedOrigins.toArray(new String[0]);
    }

}
