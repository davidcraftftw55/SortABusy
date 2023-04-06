package me.codecritter.sortabusy;

import java.util.ArrayList;

/**
 * Object for a list of TimeBlocks representing all the events in a specified date
 */
public class Schedule {

    private final ArrayList<TimeBlock> schedule;
    private final Date date;

    /**
     * Constructs a new schedule object
     * @param date date of all events this schdedule will contain
     */
    public Schedule(Date date) {
        schedule = new ArrayList<>();
        this.date = date;
    }

    /**
     * Getter method for the schdedule variable
     * @return ArrayList of TimeBlock objects representing the events on this schedule's date
     */
    public ArrayList<TimeBlock> getSchedule() {
        return schedule;
    }

    /**
     * Getter method for the date variable
     * @return Date object representing the date of all this schedule's events
     */
    public Date getDate() {
        return date;
    }
}
