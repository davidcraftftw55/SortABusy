package me.codecritter.sortabusy;

import java.util.Calendar;

public class Date {

    private final int year;
    private final int month;
    private final int day;

    public Date() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1; // in Calendar class, month starts at 0
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

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

    public long getMidnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    public long getMidnightTomorrow() {
        return getMidnight() + 86400000; // add one day
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
