package org.powertac.rachma.user.api;

import java.util.Set;

public record CreateUserData(
    String username,
    String password,
    Set<String> roles,
    String token) {}
