package org.powertac.orchestrator.game;

import java.util.ArrayList;
import java.util.List;

public class DelegatingGameSchedule implements GameSchedule {

    private final List<GameSchedule> schedules;

    public DelegatingGameSchedule() {
        schedules = new ArrayList<>();
    }

    @Override
    public Game next() {
        for (GameSchedule schedule : schedules) {
            Game next = schedule.next();
            if (null != next) {
                return next;
            }
        }
        return null;
    }

    public void register(GameSchedule schedule) {
        schedules.add(schedule);
    }

}
