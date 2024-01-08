package org.powertac.orchestrator.user;

import org.powertac.orchestrator.user.domain.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersistentUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public PersistentUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return users.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));
    }

}
