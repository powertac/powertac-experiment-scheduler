package org.powertac.rachma.security;

public class ClaimMismatchException extends TokenVerificationException {

    public ClaimMismatchException(String message) {
        super(message);
    }

}
