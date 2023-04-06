package me.codecritter.sortabusy;

import java.util.Calendar;

public class DateTime extends Date {

    private final int hour;
    private final int minute;

    public DateTime() {
        super();
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    public DateTime(int year, int month, int day, int hour, int minute) {
        super(year, month, day);
        this.hour = hour;
        this.minute = minute;
    }

    public DateTime(long epochTime) {
        super(epochTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(epochTime);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    public long getEpochTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYear(), getMonth(), getDay(), hour, minute, 0);
        return calendar.getTimeInMillis();
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
