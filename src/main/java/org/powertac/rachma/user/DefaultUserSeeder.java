package org.powertac.rachma.user;

import org.powertac.rachma.util.ID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DefaultUserSeeder {

    private final static String adminAccountName = "admin";

    @Value("${security.default.admin-password}")
    private String defaultAdminPassword;

    private final UserCrudRepository users;
    private final PasswordEncoder encoder;
    private final UserRoleRepository roles;
    private final UserRoleSeeder userRoleSeeder;

    public DefaultUserSeeder(UserCrudRepository userRepo, PasswordEncoder encoder, UserRoleRepository roles, UserRoleSeeder userRoleSeeder) {
        this.users = userRepo;
        this.encoder = encoder;
        this.roles = roles;
        this.userRoleSeeder = userRoleSeeder;
    }

    @EventListener
    public void seedOnApplicationStart(ContextRefreshedEvent event) {
        if (!users.existsById(adminAccountName)) {
            users.save(User.builder()
                .id(ID.gen())
                .username(adminAccountName)
                .password(encoder.encode(defaultAdminPassword))
                .enabled(true)
                .roles(Stream.of(getAdminRole()).collect(Collectors.toSet()))
                .build());
        }
    }

    private UserRole getAdminRole() {
        Optional<UserRole> adminRole = roles.findById(UserRoleSeeder.adminRoleName);
        if (adminRole.isEmpty()) {
            userRoleSeeder.seedAdminRole();
            adminRole = roles.findById(UserRoleSeeder.adminRoleName);
        }
        // role either already exists or is seeded in the previous step
        return adminRole.get();
    }

}
