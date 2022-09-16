package org.powertac.rachma.user;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserProviderImpl implements UserProvider {

    @Override
    public User getCurrentUser() throws UserNotFoundException { // FIXME: wrong exception type!!!!!!
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return (User) authentication.getPrincipal();
        } else {
            throw new UserNotFoundException("no user found for current request");
        }
    }

}
