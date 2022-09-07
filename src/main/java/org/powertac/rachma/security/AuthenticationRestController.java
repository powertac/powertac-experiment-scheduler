package org.powertac.rachma.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/auth")
public class AuthenticationRestController {

    private final AuthenticationManager authManager;
    private final JwtTokenService tokenFactory;
    private final UserCrudRepository users;

    public AuthenticationRestController(AuthenticationManager authManager, JwtTokenService tokenFactory, UserCrudRepository users) {
        this.authManager = authManager;
        this.tokenFactory = tokenFactory;
        this.users = users;
    }

    @GetMapping("/")
    public ResponseEntity<?> isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)
            ? ResponseEntity.ok().build()
            : ResponseEntity.status(401).build();
    }

    @PostMapping("/")
    public ResponseEntity<String> login(@RequestBody AuthenticationRequest request) {
        try {
            Authentication auth = authManager.authenticate(getAuthentication(request));
            User user = (User) auth.getPrincipal();
            Instant expirationDate = Instant.now().plus(1, ChronoUnit.DAYS);
            String token = tokenFactory.createToken(user, expirationDate);
            user.setToken(token);
            user.setTokenExpirationDate(expirationDate);
            users.save(user);
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            // TODO : log attempt
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<?> logout() {
        User user = getCurrentUser();
        if (null != user) {
            user.setToken(null);
            user.setTokenExpirationDate(null);
            users.save(user);
        }
        return ResponseEntity.ok().build();
    }

    private UsernamePasswordAuthenticationToken getAuthentication(AuthenticationRequest request) {
        return new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)
            ? (User) authentication.getPrincipal()
            : null;
    }

}
