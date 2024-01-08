package org.powertac.orchestrator.user.api;

import lombok.Getter;

import java.util.Set;

public class UpdateUserDTO {

    @Getter
    private String userId;

    @Getter
    private String password;

    @Getter
    private Set<String> roleNames;

    @Getter
    private boolean enabled;

}
