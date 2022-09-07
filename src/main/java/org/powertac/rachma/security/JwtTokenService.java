package org.powertac.rachma.security;

import com.auth0.jwt.exceptions.JWTVerificationException;

import java.time.Instant;

public interface JwtTokenService {

    String createToken(User user, Instant expirationDate);
    User getVerifiedUser(String token) throws JWTVerificationException, TokenVerificationException;

}
