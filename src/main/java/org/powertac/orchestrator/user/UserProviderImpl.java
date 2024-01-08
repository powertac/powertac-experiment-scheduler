package org.powertac.orchestrator.user;

import org.powertac.orchestrator.user.domain.User;
import org.powertac.orchestrator.user.exception.UserNotFoundException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserProviderImpl implements UserProvider {

    private final AuthenticationManager auth;

    public UserProviderImpl(AuthenticationManager auth) {
        this.auth = auth;
    }

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
