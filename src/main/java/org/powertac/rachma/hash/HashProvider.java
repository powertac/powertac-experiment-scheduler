package org.powertac.rachma.hash;

public interface HashProvider<T> {

    String getHash(T entity);

}
