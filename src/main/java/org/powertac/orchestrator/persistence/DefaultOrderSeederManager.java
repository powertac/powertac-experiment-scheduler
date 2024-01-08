package org.powertac.orchestrator.persistence;

import org.powertac.orchestrator.user.DefaultUserSeeder;
import org.powertac.orchestrator.user.UserRoleSeeder;
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
