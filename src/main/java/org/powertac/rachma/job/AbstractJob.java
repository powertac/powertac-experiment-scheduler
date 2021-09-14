package org.powertac.rachma.job;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@NoArgsConstructor
@AllArgsConstructor
@Deprecated
abstract public class AbstractJob implements Job {

    @Getter
    @Setter
    @Id
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private JobStatus status = new JobStatusImpl();

}
