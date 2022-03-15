package org.powertac.rachma.file.action;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;

public class FileTransaction {

    private final LinkedList<FileAction> actions = new LinkedList<>();
    private final Logger logger = LogManager.getLogger(FileTransaction.class);

    public FileTransaction copy(Path source, Path target) {
        actions.add(new CopyFileAction(source, target));
        return this;
    }

    public FileTransaction move(Path source, Path target) {
        actions.add(new MoveFileAction(source, target));
        return this;
    }

    public FileTransaction delete(Path path) {
        actions.add(new DeleteFileAction(path));
        return this;
    }

    public FileTransaction mkdir(Path path) {
        actions.add(new MkdirFileAction(path));
        return this;
    }

    public void commit() throws IOException {
        exec(); // first execute the action; proceed if that throws no errors
        LinkedList<FileAction> committedActions = new LinkedList<>();
        try {
            while (null != actions.peekFirst()) {
                FileAction action = actions.removeFirst();
                action.commit();
                committedActions.add(action);
            }
        } catch (IOException e) {
            rollback(committedActions);
            throw e;
        }
    }

    private void exec() throws IOException {
        LinkedList<FileAction> executedActions = new LinkedList<>();
        try {
            while (null != actions.peekFirst()) {
                FileAction action = actions.removeFirst();
                action.exec();
                executedActions.add(action);
            }
        } catch (IOException e) {
            rollback(executedActions);
            throw e;
        }
    }

    private void rollback(LinkedList<FileAction> actionList) {
        try {
            while (null != actionList.peekLast()) { // actions are rolled back in reverse order
                FileAction action = actionList.removeLast();
                action.rollback();
            }
        } catch (IOException e) {
            logger.warn(e);
        }
    }

}
