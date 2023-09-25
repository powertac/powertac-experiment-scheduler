package org.powertac.rachma.user;

public interface UserProvider {

    User getCurrentUser() throws UserNotFoundException;

}
