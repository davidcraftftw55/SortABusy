package me.codecritter.sortabusy;

import java.util.Calendar;

/**
 * Object representing a specific date (contains year, month, and day)
 */
public class Date {

    private final int year;
    private final int month;
    private final int day;

    /**
     * Constructs a new Date object, representing today's date
     */
    public Date() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1; // in Calendar class, month starts at 0
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Constructs a new Date object, using the date of the epoch time param
     * @param epochTime time in milliseconds since the epoch that is within the date this object
     *                  should represent
     */
    public Date(long epochTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(epochTime);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1; // in Calendar class, month starts at 0
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Constructs a new Date object, with a specified date
     * @param year numerical representation of the year of this date (ie- 2023)
     * @param month numerical representation of the month of this date (ie- 2 for Febuary)
     * @param day numerical representation of the day of this date (ie- 6)
     */
    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Returns the word that best represents this Date: "Today" "Yesterday" "Last Wednesday", etc
     * @return the aforementioned word
     */
    public String getDayName() {
        int daysAway = getDifference();
        if (-7 <= daysAway && daysAway <= -2) {
            return "Last " + getWeekday();
        } else if (daysAway == -1) {
            return "Yesterday";
        } else if (daysAway == 0) {
            return "Today";
        } else if (daysAway == 1) {
            return "Tomorrow";
        } else if (2 <= daysAway && daysAway <= 6) {
            return getWeekday();
        } else if (7 <= daysAway && daysAway <= 13) {
            return "Next " + getWeekday();
        } else {
            return month + "/" + day + "/" + year;
        }
    }

    /**
     * Generates a new Date object representing the day after this Date
     * @return new Date object as specified above
     */
    public Date getTomorrow() {
        int year = this.year;
        int month = this.month;
        int day = this.day + 1;

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day > 31) {
                    day -= 31;
                    month++;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (day > 30) {
                    day -= 30;
                    month++;
                }
                break;
            case 2:
                int daysInFeb = (year % 4 == 0) ? 29 : 28;
                if (day > daysInFeb) {
                    day -= daysInFeb;
                    month++;
                }
                break;
        }
        if (month > 12) {
            month -= 12;
            year++;
        }
        return new Date(year, month, day);
    }

    /**
     * Generates a new Date object representing the day before this Date
     * @return new Date object as specified above
     */
    public Date getYesterday() {
        int year = this.year;
        int month = this.month;
        int day = this.day - 1;

        if (day < 1) {
            switch (month) {
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    // previous month has 30 days
                    day = 30;
                    month--;
                    break;
                case 2:
                case 4:
                case 6:
                case 9:
                case 11:
                    // previous month has 31 days
                    day = 31;
                    month--;
                    break;
                case 3:
                    // previous month is Febuary
                    day = (year % 4 == 0) ? 29 : 28;
                    month--;
                    break;
                case 1:
                    // prev month was last year
                    day = 31;
                    month = 12;
                    year--;
                    break;
            }
        }
        return new Date(year, month, day);
    }

    /**
     * Gets midnight at this start of this day
     * @return midnight today, in milliseconds since the epoch
     */
    public long getMidnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Gets midnight at the end of this day (essentially midnight tomorrow)
     * @return midnight tomorrow, in milliseconds since the epoch
     */
    public long getMidnightTomorrow() {
        return getMidnight() + 86400000; // add one day
    }

    /**
     * Getter method for the year variable
     * @return numerical representation of the year of the Date represented by this
     */
    public int getYear() {
        return year;
    }

    /**
     * Getter method for the month variable
     * @return numerical representation of the month of the Date represented by this (ie- 2 is Feb)
     */
    public int getMonth() {
        return month;
    }

    /**
     * Getter method for the day variable
     * @return numerical representation of the day of the Date represented by this
     */
    public int getDay() {
        return day;
    }

    /**
     * Gets number of days this date is before or after today
     * @return number of days between the 2 days; negative if this date is before date param,
     * positive if this date is after date param
     */
    private int getDifference() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        Calendar other = Calendar.getInstance();
        Date date = new Date();
        other.set(date.year, date.month - 1, date.day, 0, 0, 0);
        long difference = calendar.getTimeInMillis() - other.getTimeInMillis();
        return (int) difference / 86400000;
    }

    private String getWeekday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        switch (weekday) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
        }
        return "";
    }
}
