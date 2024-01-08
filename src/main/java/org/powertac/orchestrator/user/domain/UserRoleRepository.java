package org.powertac.orchestrator.user.domain;

import org.springframework.data.repository.CrudRepository;

public interface UserRoleRepository extends CrudRepository<UserRole, String> {

    boolean existsByName(String name);

}
