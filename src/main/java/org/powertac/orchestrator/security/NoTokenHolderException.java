package org.powertac.orchestrator.security;

public class NoTokenHolderException extends TokenVerificationException {

    public NoTokenHolderException(String message) {
        super(message);
    }

}
