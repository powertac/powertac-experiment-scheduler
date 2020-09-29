package org.powertac.rachma.hash;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class GenericHashProvider implements HashProvider {

    @Override
    public String getHash(Object entity) {
        return Hashing.sha256()
            .hashString(entity.toString(), StandardCharsets.UTF_8)
            .toString();
    }
}
