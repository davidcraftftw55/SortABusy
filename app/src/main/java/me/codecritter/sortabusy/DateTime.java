package me.codecritter.sortabusy;

import androidx.annotation.Nullable;

import java.util.Calendar;

/**
 * An object that represents a date and time (inherits the Date)
 */
public class DateTime extends Date {

    private final int hour;
    private final int minute;

    /**
     * Constructs a new DateTime object, with parameters specifying the datetime
     * @param year numerical representation of the year of this DateTime (ie- 2023)
     * @param month numerical representation of the month of this DateTime (ie- 2 is Febuary)
     * @param day numerical representation of the day of this DateTime (ie- 6)
     * @param hour numerical representation of the hour of this DateTime (ie- 22 for 10pm)
     * @param minute numerical representation of the minute of this DateTime (ie- for 10:40, this would be 40)
     */
    public DateTime(int year, int month, int day, int hour, int minute) {
        super(year, month, day);
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Constructs a new DateTime object, with the time specified by number of milliseconds since the epoch
     * @param epochTime time to instantiate this DateTime object to, in milliseconds since the epoch
     */
    public DateTime(long epochTime) {
        super(epochTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(epochTime);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    /**
     * Determines whether this DateTime is before to other DateTime param
     * @param other DateTime to compare this DateTime to
     * @return true if this DateTime is before to other DateTime param, false otherwise
     */
    public boolean isBefore(DateTime other) {
        if (getYear() < other.getYear()) {
            return true;
        } else if (getYear() > other.getYear()) {
            return false;
        }

        if (getMonth() < other.getMonth()) {
            return true;
        } else if (getMonth() > other.getMonth()) {
            return false;
        }

        if (getDay() < other.getDay()) {
            return true;
        } else if (getDay() > other.getDay()) {
            return false;
        }

        if (getHour() < other.getHour()) {
            return true;
        } else if (getHour() > other.getHour()) {
            return false;
        }

        return getMinute() < other.getMinute();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof DateTime) {
            DateTime other = (DateTime) obj;
            return getYear() == other.getYear() && getMonth() == other.getMonth()
                    && getDay() == other.getDay() && getHour() == other.getHour()
                    && getMinute() == other.getMinute();
        }
        return false;
    }

    /**
     * Converts and returns this DateTime object in the form of milliseconds since the epoch
     * @return the datetime represented by this in the form of milliseconds since the epoch
     */
    public long getEpochTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYear(), getMonth() - 1, getDay(), hour, minute, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Getter method for the hour variable
     * @return hour of this DateTime (ie- 22 means 10pm)
     */
    public int getHour() {
        return hour;
    }

    /**
     * Getter method for the minute variable
     * @return minute of this DateTime (ie- 10:40 would return 40)
     */
    public int getMinute() {
        return minute;
    }
}
