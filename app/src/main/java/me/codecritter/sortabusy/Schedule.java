package me.codecritter.sortabusy;

import java.util.ArrayList;

public class Schedule {

    private final ArrayList<TimeBlock> schedule;
    private final Date date;

    public Schedule(Date date) {
        schedule = new ArrayList<>();
        this.date = date;
    }

    public ArrayList<TimeBlock> getSchedule() {
        return schedule;
    }

    public Date getDate() {
        return date;
    }
}
