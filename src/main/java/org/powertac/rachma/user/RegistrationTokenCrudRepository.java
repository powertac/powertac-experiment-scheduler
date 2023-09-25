package org.powertac.rachma.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RegistrationTokenCrudRepository extends CrudRepository<RegistrationToken, Long> {

    Optional<RegistrationToken> findByToken(String token);

}
