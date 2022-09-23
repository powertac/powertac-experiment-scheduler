package org.powertac.rachma.user;

import org.powertac.rachma.persistence.Seeder;
import org.powertac.rachma.persistence.SeederException;
import org.powertac.rachma.util.ID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DefaultUserSeeder implements Seeder {

    private final static String adminAccountName = "admin";

    @Value("${security.default.admin-password}")
    private String defaultAdminPassword;

    private final UserCrudRepository users;
    private final PasswordEncoder encoder;
    private final UserRoleRepository roles;

    public DefaultUserSeeder(UserCrudRepository userRepo, PasswordEncoder encoder, UserRoleRepository roles) {
        this.users = userRepo;
        this.encoder = encoder;
        this.roles = roles;
    }

    @Override
    public void seed() throws SeederException {
        if (!users.existsByUsername(adminAccountName)) {
            Optional<UserRole> adminRole = roles.findById(UserRoleSeeder.adminRoleName);
            if (adminRole.isEmpty()) {
                throw new SeederException("admin role has not been seeded");
            }
            users.save(User.builder()
                .id(ID.gen())
                .username(adminAccountName)
                .password(encoder.encode(defaultAdminPassword))
                .enabled(true)
                .roles(List.of(adminRole.get()))
                .build());
        }
    }

}
