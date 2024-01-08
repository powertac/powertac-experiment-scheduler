package org.powertac.orchestrator.security;

public class ExpiredTokenException extends TokenVerificationException {

    public ExpiredTokenException(String message) {
        super(message);
    }

}
