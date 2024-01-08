package org.powertac.orchestrator.file.action;

import java.io.IOException;

/**
 * File actions are intended to implement some sort of (very wonky) transaction properties. The main goal at this time
 * is not to lose files while executing a large set of file actions.
 */
public interface FileAction {

    void exec() throws IOException;
    void commit() throws IOException;
    void rollback() throws IOException;

}
