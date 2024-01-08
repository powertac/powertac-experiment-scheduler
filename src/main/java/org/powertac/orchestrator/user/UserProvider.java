package org.powertac.orchestrator.user;

import org.powertac.orchestrator.user.domain.User;
import org.powertac.orchestrator.user.exception.UserNotFoundException;

public interface UserProvider {

    User getCurrentUser() throws UserNotFoundException;

}
