package org.powertac.rachma.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.powertac.rachma.file.DownloadToken;
import org.powertac.rachma.user.exception.InvalidRegistrationTokenException;
import org.powertac.rachma.user.domain.RegistrationToken;
import org.powertac.rachma.user.domain.User;

import java.time.Instant;

public interface JwtTokenService {

    String createAuthToken(User user, Instant expirationDate);
    String createRegistrationToken(Instant expirationDate);
    String createDownloadToken(User user, String filePath);
    User getVerifiedUser(String token) throws JWTVerificationException, TokenVerificationException;
    RegistrationToken getVerifiedRegistrationToken(String token) throws InvalidRegistrationTokenException;
    DownloadToken getVerifiedDownloadToken(String token) throws TokenVerificationException;

}
