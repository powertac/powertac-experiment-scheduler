package org.powertac.orchestrator.game;

import org.springframework.stereotype.Component;

@Component
public class FifoGameSchedule implements GameSchedule {

    private final GameRepository games;

    public FifoGameSchedule(GameRepository games) {
        this.games = games;
    }

    @Override
    public Game next() {
        return games.findFirstQueued();
    }
}
