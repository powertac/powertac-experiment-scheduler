package org.powertac.rachma.security;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ExampleUserSeeder {

    private final UserCrudRepository users;
    private final PasswordEncoder encoder;
    private final UserRoleRepository roles;

    public ExampleUserSeeder(UserCrudRepository userRepo, PasswordEncoder encoder, UserRoleRepository roles) {
        this.users = userRepo;
        this.encoder = encoder;
        this.roles = roles;
    }

    @EventListener
    public void seedOnApplicationStart(ContextRefreshedEvent event) {
        UserRole admin = new UserRole("ADMIN");
        users.save(new User(
            null,
            "admin",
            encoder.encode("supersecure"),
            null,
            null,
            true,
            Stream.of(admin).collect(Collectors.toSet())));
    }

    @PreDestroy
    private void removeTeams() {
        users.deleteAll();
        roles.deleteAll();
    }

}
