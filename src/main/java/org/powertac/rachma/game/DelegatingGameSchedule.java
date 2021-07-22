package org.powertac.rachma.game;

import java.util.ArrayList;
import java.util.List;

public class DelegatingGameSchedule implements GameSchedule {

    private final List<GameSchedule> schedules;

    public DelegatingGameSchedule() {
        schedules = new ArrayList<>();
    }

    @Override
    public Game next() {
        return null;
    }

    public void register(GameSchedule schedule) {

    }

}
