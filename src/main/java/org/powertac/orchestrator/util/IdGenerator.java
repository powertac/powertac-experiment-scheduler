package org.powertac.orchestrator.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdGenerator implements IdProvider {

    @Override
    public String getAnyId() {
        return generateId();
    }

    public static String generateId() {
        return UUID.randomUUID().toString().substring(0,7);
    }

}
