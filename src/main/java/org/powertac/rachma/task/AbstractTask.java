package org.powertac.rachma.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.powertac.rachma.job.Job;
import org.powertac.rachma.resource.WorkDirectory;
import org.springframework.data.annotation.Transient;

abstract public class AbstractTask implements Task {

    @Getter
    protected final String id;

    @Getter
    @JsonIgnore
    @Transient
    protected final Job job;

    @Getter
    protected final GenericTaskStatus status = new GenericTaskStatus();

    public AbstractTask(String id , Job job) {
        this.id = id;
        this.job = job;
    }

    @Override
    public WorkDirectory getWorkDirectory() {
        return WorkDirectory.fromParent(job.getWorkDirectory(), id);
    }

}
