package org.powertac.rachma.logprocessor;

import org.powertac.rachma.game.Game;
import org.powertac.rachma.user.User;
import org.powertac.rachma.util.ID;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
public class LogProcessorTaskFactory {

    public LogProcessorTask create(User user, Game game, Set<String> processorIds) {
        return LogProcessorTask.builder()
            .id(ID.gen())
            .createdAt(Instant.now())
            .creator(user)
            .game(game)
            .processorIds(processorIds)
            .build();
    }

}
