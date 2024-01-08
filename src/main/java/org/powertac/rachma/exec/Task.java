package org.powertac.rachma.exec;

import org.powertac.rachma.user.domain.User;

import java.time.Instant;

public interface Task {

    String getId();
    User getCreator();
    Instant getCreatedAt();
    Instant getStart();
    Instant getEnd();
    Integer getPriority();
    boolean hasFailed();

}
