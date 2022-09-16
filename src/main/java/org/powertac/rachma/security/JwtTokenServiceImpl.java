package org.powertac.rachma.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.powertac.rachma.user.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class JwtTokenServiceImpl implements JwtTokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final UserCrudRepository users;
    private final RegistrationTokenCrudRepository registrationTokens;

    public JwtTokenServiceImpl(Algorithm algorithm, JWTVerifier verifier, UserCrudRepository users, RegistrationTokenCrudRepository registrationTokens) {
        this.algorithm = algorithm;
        this.verifier = verifier;
        this.users = users;
        this.registrationTokens = registrationTokens;
    }

    @Override
    public String createAuthToken(User user, Instant expirationDate) {
        return JWT.create()
            .withIssuer("powertac")
            .withClaim("username", user.getUsername())
            .withClaim("expiration", expirationDate.getEpochSecond())
            .sign(algorithm);
    }

    @Override
    public String createRegistrationToken(Instant expirationDate) {
        return JWT.create()
            .withIssuer("powertac")
            .withClaim("expiration", expirationDate.getEpochSecond())
            .sign(algorithm);
    }

    @Override
    public User getVerifiedUser(String token) throws JWTVerificationException, TokenVerificationException {
        DecodedJWT jwt = verifier.verify(token);
        User user = users.findByToken(token).orElse(null);
        if (null == user) {
            throw new NoTokenHolderException(String.format("no holder for token '%s' found", token));
        }
        verifyUsernameClaim(jwt, user.getUsername());
        verifyExpirationDateClaim(jwt, user.getTokenExpirationDate());
        verifyExpirationDate(user.getTokenExpirationDate());
        return user;
    }

    public RegistrationToken getVerifiedRegistrationToken(String token) throws InvalidRegistrationTokenException {
        DecodedJWT jwt = verifier.verify(token);
        Optional<RegistrationToken> registrationToken = registrationTokens.findByToken(token);
        if (registrationToken.isPresent()) {
            try {
                // FIXME : check if token already claimed!!!
                verifyExpirationDateClaim(jwt, registrationToken.get().getExpirationDate());
                verifyExpirationDate(registrationToken.get().getExpirationDate());
                return registrationToken.get();
            } catch (JWTVerificationException|TokenVerificationException e) {
                throw new InvalidRegistrationTokenException("invalid registration token", e);
            }
        } else {
            throw new InvalidRegistrationTokenException(String.format("no registration token found for token '%s'", token));
        }
    }

    private void verifyUsernameClaim(DecodedJWT jwt, String username) throws ClaimMismatchException {
        String claimedUsername = jwt.getClaim("username").asString();
        if (!claimedUsername.equals(username)) {
            throw new ClaimMismatchException(
                String.format("claimed username '%s' does not match token holder's username '%s'",
                    claimedUsername,
                    username));
        }
    }

    private void verifyExpirationDateClaim(DecodedJWT jwt, Instant expirationDate) throws ClaimMismatchException {
        Instant claimedExpirationDate = Instant.ofEpochSecond(jwt.getClaim("expiration").asLong());
        if (!claimedExpirationDate.equals(expirationDate)) {
            throw new ClaimMismatchException(
                String.format("claimed expiration date '%s' does not match token holder's expiration date '%s'",
                    claimedExpirationDate,
                    expirationDate));
        }
    }

    private void verifyExpirationDate(Instant expirationDate) throws ExpiredTokenException {
        if (Instant.now().isAfter(expirationDate)) {
            throw new ExpiredTokenException("token has expired");
        }
    }

}
