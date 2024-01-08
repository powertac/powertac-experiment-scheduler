package org.powertac.rachma.user;

import org.powertac.rachma.user.domain.User;
import org.powertac.rachma.user.exception.UserNotFoundException;

public interface UserProvider {

    User getCurrentUser() throws UserNotFoundException;

}
