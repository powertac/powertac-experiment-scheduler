package org.powertac.rachma.user;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserRoleSeeder {

    public final static String adminRoleName = "ADMIN";
    private final static Set<String> roleNames = Stream.of(
        adminRoleName,
        "AUTHENTICATED"
    ).collect(Collectors.toSet());

    private final UserRoleRepository roles;

    public UserRoleSeeder(UserRoleRepository roles) {
        this.roles = roles;
    }

    @EventListener
    public void seedOnApplicationStart(ContextRefreshedEvent event) {
        for (String name : roleNames) {
            createOrSkipRole(name);
        }
    }

    public void seedAdminRole() {
        createOrSkipRole(adminRoleName);
    }

    private void createOrSkipRole(String name) {
        if (!roles.existsById(name)) {
            roles.save(new UserRole(name));
        }
    }

}
