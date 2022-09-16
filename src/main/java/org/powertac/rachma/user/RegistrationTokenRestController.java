package org.powertac.rachma.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.security.JwtTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/registration")
public class RegistrationTokenRestController {

    private final static Duration expirationPeriod = Duration.ofDays(7);

    private final JwtTokenService tokenService;
    private final UserProvider userProvider;
    private final RegistrationTokenCrudRepository registrationTokens;
    private final Logger logger;

    public RegistrationTokenRestController(JwtTokenService tokenService, UserProvider userProvider,
                                           RegistrationTokenCrudRepository registrationTokens) {
        this.tokenService = tokenService;
        this.userProvider = userProvider;
        this.registrationTokens = registrationTokens;
        logger = LogManager.getLogger(RegistrationTokenRestController.class);
    }

    @PostMapping("/")
    public ResponseEntity<String> createRegistrationToken() {
        try {
            Instant expiration = Instant.now().plus(expirationPeriod);
            String tokenString = tokenService.createRegistrationToken(expiration);
            RegistrationToken token = RegistrationToken.builder()
                .token(tokenString)
                .issuedBy(userProvider.getCurrentUser())
                .issuedAt(Instant.now())
                .expirationDate(expiration)
                .build();
            registrationTokens.save(token);
            return ResponseEntity.ok(tokenString);
        } catch (UserNotFoundException e) {
            logger.error(e);
            return ResponseEntity.status(401).build();
        }
    }

}
