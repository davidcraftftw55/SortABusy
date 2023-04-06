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
}
