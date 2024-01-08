package org.powertac.rachma.user;

import org.powertac.rachma.persistence.Seeder;
import org.powertac.rachma.user.domain.UserRole;
import org.powertac.rachma.user.domain.UserRoleRepository;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserRoleSeeder implements Seeder {

    public final static String adminRoleName = "ADMIN";
    private final static Set<String> roleNames = Set.of(
        adminRoleName,
        "BASE",
        "AUTHENTICATED" // FIXME : DEPRECATE
    );

    private final UserRoleRepository roles;

    public UserRoleSeeder(UserRoleRepository roles) {
        this.roles = roles;
    }

    @Override
    public void seed() {
        for (String name : roleNames) {
            createOrSkipRole(name);
        }
    }

    private void createOrSkipRole(String name) {
        if (!roles.existsById(name)) {
            roles.save(new UserRole(name));
        }
    }

}
