package org.powertac.orchestrator.validation;

import java.util.regex.Pattern;

public class UUIDValidator {

    private final static Pattern uuidPattern = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    public static boolean isValid(String uuid) {
        return null != uuid && uuidPattern.matcher(uuid).matches();
    }

}
