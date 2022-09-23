package org.powertac.rachma.persistence;

import org.powertac.rachma.user.DefaultUserSeeder;
import org.powertac.rachma.user.UserRoleSeeder;
import org.springframework.stereotype.Service;

@Service
public class DefaultOrderSeederManager implements SeederManager {

    private final UserRoleSeeder roleSeeder;
    private final DefaultUserSeeder userSeeder;

    public DefaultOrderSeederManager(UserRoleSeeder roleSeeder, DefaultUserSeeder userSeeder) {
        this.roleSeeder = roleSeeder;
        this.userSeeder = userSeeder;
    }

    @Override
    public void runSeeders() throws SeederException {
        roleSeeder.seed();
        userSeeder.seed();
    }

}
