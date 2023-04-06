package me.codecritter.sortabusy;

/**
 * Object containing all the data of an event that will be represented in the ScheduleMaker as time blocks
 */
public class TimeBlock {

    private final long eventId;
    private boolean changed;
    private String name;
    private DateTime start;
    private DateTime end;

    /**
     * Constructs a new TimeBlock, containing the data of an event
     * @param eventId id of the event in the CalendarContract Events table
     * @param name title of the event
     * @param start DateTime object representing the start time of the event
     * @param end DateTime object representing the end time of the event
     */
    public TimeBlock(long eventId, String name, DateTime start, DateTime end) {
        this.eventId = eventId;
        changed = eventId == -1; // if id is -1, this is a new event, should be considered "changed"
        this.name = name;
        this.start = start;
        this.end = end;
    }

    /**
     * Getter method for the eventId varaible
     * @return id of the event in the CalendarContract Events table
     */
    public long getEventId() {
        return eventId;
    }

    /**
     * Getter method for the changed variable
     * @return true if this object has changes that weren't saved to the CalendarContract, false otherwise
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Getter method for the name variable
     * @return title of the event represented by this TimeBlock
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method for the start variable
     * @return DateTime object representing the start time of the event represented by this TimeBlock
     */
    public DateTime getStart() {
        return start;
    }

    /**
     * Getter method for the End variable
     * @return DateTime object representing the end time of the event represented by this TimeBlock
     */
    public DateTime getEnd() {
        return end;
    }

    /**
     * Setter method for the name variable
     * @param name new title for the event represented by this TimeBlock
     */
    public void setName(String name) {
        this.name = name;
        changed = true;
    }

    /**
     * Setter method for the start variable
     * @param start new DateTime object representing the new start time for the event represented by this TimeBlock
     */
    public void setStart(DateTime start) {
        this.start = start;
        changed = true;
    }

    /**
     * Setter method for the end variable
     * @param end new DateTime object representing the new end time for the event represented by this TimeBlock
     */
    public void setEnd(DateTime end) {
        this.end = end;
        changed = true;
    }

    /**
     * Marks that the changes in this event have been saved to the CalendarContract (setting the "changed" variable to false)
     */
    public void markAsUpdated() {
        changed = false;
    }
}
