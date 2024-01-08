package org.powertac.orchestrator.util;

import java.util.UUID;

public final class ID {

    public static String gen() {
        return UUID.randomUUID().toString();
    }

}
