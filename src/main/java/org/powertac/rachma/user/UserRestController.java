package org.powertac.rachma.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.security.JwtTokenService;
import org.powertac.rachma.util.ID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserProvider userProvider;
    private final JwtTokenService tokenService;
    private final UserCrudRepository users;
    private final RegistrationTokenCrudRepository registrationTokens;
    private final UserRoleRepository userRoles;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger;

    public UserRestController(UserProvider userProvider, JwtTokenService tokenService, UserCrudRepository users,
                              RegistrationTokenCrudRepository registrationTokens, UserRoleRepository userRoles,
                              PasswordEncoder passwordEncoder) {
        this.userProvider = userProvider;
        this.tokenService = tokenService;
        this.users = users;
        this.registrationTokens = registrationTokens;
        this.userRoles = userRoles;
        this.passwordEncoder = passwordEncoder;
        logger = LogManager.getLogger(UserRestController.class);
    }

    @GetMapping("/")
    public ResponseEntity<Iterable<User>> getUsers() {
        try {
            User currentUser = userProvider.getCurrentUser();
            if (currentUser.hasAuthority("ADMIN")) {
                return ResponseEntity.ok(users.findAll());
            } else {
                return ResponseEntity.status(403).build();
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/current")
    public ResponseEntity<User> getAuthenticatedUser() {
        try {
            return ResponseEntity.ok(userProvider.getCurrentUser());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody CreateUserDTO userData) {
        try {
            RegistrationToken registrationToken = tokenService.getVerifiedRegistrationToken(userData.getRegistrationToken());
            Set<UserRole> roles = loadRoles(userData.getRoleNames());
            User user = buildUser(userData.getUsername(), userData.getPassword(), roles);
            users.save(user);
            claimRegistrationToken(user, registrationToken);
            return ResponseEntity.ok(user);
        } catch (InvalidRegistrationTokenException|UserRoleNotFoundException e) {
            logger.error("unable to create user", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/")
    public ResponseEntity<User> updateUser(@RequestBody UpdateUserDTO userData) {
        try {
            User currentUser = userProvider.getCurrentUser();
            Optional<User> userOptional = users.findById(userData.getUserId());
            if (userOptional.isEmpty()) {
                if (currentUser.hasAuthority("ADMIN")) {
                    logger.error("update on non-existing user"); // TODO : structured logging
                    return ResponseEntity.badRequest().build();
                } else {
                    logger.error("unauthorized update on non-existing user"); // TODO : structured logging
                    return ResponseEntity.status(403).build();
                }
            } else {
                User user;
                if (currentUser.hasAuthority("ADMIN")) {
                     user = getUpdatedUser(userOptional.get(), userData, true);
                } else if (userOptional.get().getId().equals(userProvider.getCurrentUser().getId())) {
                    user = getUpdatedUser(userOptional.get(), userData, false);
                } else {
                    logger.error("unauthorized user update"); // TODO : structured logging
                    return ResponseEntity.status(403).build();
                }
                users.save(user);
                return ResponseEntity.ok(user);
            }
        } catch (UserRoleNotFoundException|UserNotFoundException e) {
            logger.error("unable to update user", e);
            return ResponseEntity.badRequest().build();
        }
    }

    private User buildUser(String username, String password, Set<UserRole> roles) {
        return User.builder()
            .id(ID.gen())
            .username(username)
            .password(passwordEncoder.encode(password))
            .roles(roles)
            .enabled(true)
            .build();
    }

    private void claimRegistrationToken(User user, RegistrationToken token) {
        token.setClaimedBy(user);
        token.setClaimedAt(Instant.now());
        registrationTokens.save(token);
    }

    private Set<UserRole> loadRoles(Set<String> roleNames) throws UserRoleNotFoundException {
        Set<UserRole> roles = new HashSet<>();
        for (String name : roleNames) {
            Optional<UserRole> role = userRoles.findById(name);
            if (role.isEmpty()) {
                throw new UserRoleNotFoundException(String.format("user role '%s' not found", name));
            }
            roles.add(role.get());
        }
        return roles;
    }

    private User getUpdatedUser(User user, UpdateUserDTO userData, boolean fullUpdate) throws UserRoleNotFoundException {
        if (fullUpdate) {
            user.getRoles().addAll(loadRoles(userData.getRoleNames()));
            user.setEnabled(userData.isEnabled());
        }
        if (null != userData.getPassword()) {
            user.setPassword(passwordEncoder.encode(userData.getPassword())); // TODO : enforce password rules
        }
        return user;
    }

}
