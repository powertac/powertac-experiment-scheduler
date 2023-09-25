package org.powertac.rachma.security;

public class ExpiredTokenException extends TokenVerificationException {

    public ExpiredTokenException(String message) {
        super(message);
    }

}
