package org.powertac.rachma.security;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AuthenticationRequest {

    @Getter
    private String username;

    @Getter
    private String password;

}
