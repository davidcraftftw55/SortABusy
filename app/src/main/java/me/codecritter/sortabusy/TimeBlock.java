package me.codecritter.sortabusy;

import android.graphics.Color;

public class TimeBlock {

    private final long eventId;
    private boolean changed;
    private String name;
    private Category category; // TASK to be implemented
    private Color color; // TASK to be implemented
    private DateTime start;
    private DateTime end;

    public TimeBlock(long eventId, String name, DateTime start, DateTime end) {
        this.eventId = eventId;
        changed = eventId == -1; // if id is -1, this is a new event, should be considered "changed"
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public long getEventId() {
        return eventId;
    }

    public boolean isChanged() {
        return changed;
    }

    public String getName() {
        return name;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setName(String name) {
        this.name = name;
        changed = true;
    }

    public void setStart(DateTime start) {
        this.start = start;
        changed = true;
    }

    public void setEnd(DateTime end) {
        this.end = end;
        changed = true;
    }
}
