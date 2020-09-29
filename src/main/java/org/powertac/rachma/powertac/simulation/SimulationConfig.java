package org.powertac.rachma.powertac.simulation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.powertac.rachma.powertac.server.ServerParameterValidator;
import org.powertac.rachma.util.JsonListLoader;
import org.powertac.rachma.validation.ValidationRuleRepository;
import org.powertac.rachma.validation.exception.ValidationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class SimulationConfig {

    private final ObjectMapper mapper;
    private final JsonListLoader listLoader;

    private final Set<String> blacklist= new HashSet<>(Stream.of(
        "server.jmsManagementService.jmsBrokerUrl",
        "server.logfileSuffix",
        "server.competitionControlService.loginTimeout",
        "server.competitionControlService.stackTraceDepth"
    ).collect(Collectors.toSet()));

    public SimulationConfig(ObjectMapper mapper, JsonListLoader listLoader) {
        this.mapper = mapper;
        this.listLoader = listLoader;
    }

    @Bean
    public SimulationParameterValidator simulationParameterValidator() throws IOException {
        ValidationRuleRepository rules = new ValidationRuleRepository();
        configureBlacklist(rules);
        List<String> whitelist = listLoader.loadResource("editable-server-properties.names.json");
        return new ServerParameterValidator(rules, new HashSet<>(whitelist));
    }

    private void configureBlacklist(ValidationRuleRepository rules) {
        blacklist.forEach(parameter ->
            rules.add(parameter, (Object value) -> {
                throw new ValidationException(value, String.format("parameter '%s' is not supported", parameter));
            }));
    }

}
