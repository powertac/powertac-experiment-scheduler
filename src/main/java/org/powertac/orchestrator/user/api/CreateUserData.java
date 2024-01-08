package org.powertac.orchestrator.user.api;

import java.util.Set;

public record CreateUserData(
    String username,
    String password,
    Set<String> roles,
    String token) {}
