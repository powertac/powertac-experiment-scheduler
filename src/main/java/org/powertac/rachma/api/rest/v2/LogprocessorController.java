package org.powertac.rachma.api.rest.v2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.powertac.rachma.logprocessor.LogProcessorTask;
import org.powertac.rachma.logprocessor.LogProcessorTaskFactory;
import org.powertac.rachma.logprocessor.NewLogProcessorTaskDTO;
import org.powertac.rachma.user.User;
import org.powertac.rachma.user.UserNotFoundException;
import org.powertac.rachma.user.UserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/log-processor/")
public class LogprocessorController {

    private final UserProvider userProvider;
    private final LogProcessorTaskFactory factory;
    private final Logger logger;

    public LogprocessorController(UserProvider userProvider, LogProcessorTaskFactory factory) {
        this.userProvider = userProvider;
        this.factory = factory;
        logger = LogManager.getLogger(LogprocessorController.class);
    }

    @PostMapping("/")
    public ResponseEntity<LogProcessorTask> processLog(@RequestBody NewLogProcessorTaskDTO dto) {
        try {
            User user = userProvider.getCurrentUser();
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            logger.error("unable to determine current user", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}
