package org.powertac.rachma.user;

import lombok.Getter;

import java.util.Set;

public class CreateUserDTO {

    @Getter
    private String username;

    @Getter
    private String password;

    @Getter
    private Set<String> roleNames;

    @Getter
    private String registrationToken;

}
