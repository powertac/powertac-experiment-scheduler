package org.powertac.rachma.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserCrudRepository extends CrudRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByToken(String token);

}
