package org.powertac.orchestrator.security;

public class ClaimMismatchException extends TokenVerificationException {

    public ClaimMismatchException(String message) {
        super(message);
    }

}
