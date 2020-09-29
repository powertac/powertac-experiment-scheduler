package org.powertac.rachma.runner;

import org.powertac.rachma.runner.exception.RunnerCreationFailedException;

public interface RunnerFactory<T extends RunnableEntity, S extends Runner> {

    S createRunner(T entity) throws RunnerCreationFailedException;

}
