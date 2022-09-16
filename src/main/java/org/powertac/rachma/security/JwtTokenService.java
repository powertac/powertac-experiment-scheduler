package org.powertac.rachma.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.powertac.rachma.user.InvalidRegistrationTokenException;
import org.powertac.rachma.user.RegistrationToken;
import org.powertac.rachma.user.User;

import java.time.Instant;

public interface JwtTokenService {

    String createAuthToken(User user, Instant expirationDate);
    String createRegistrationToken(Instant expirationDate);
    User getVerifiedUser(String token) throws JWTVerificationException, TokenVerificationException;
    RegistrationToken getVerifiedRegistrationToken(String token) throws InvalidRegistrationTokenException;

}
