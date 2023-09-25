package org.powertac.rachma.security;

public class NoTokenHolderException extends TokenVerificationException {

    public NoTokenHolderException(String message) {
        super(message);
    }

}
