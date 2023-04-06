package me.codecritter.sortabusy;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarHelper {

    private static CalendarHelper instance;

    public static CalendarHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CalendarHelper(context);
        }
        return instance;
    }

    private long calendarId;
    private String timezone;


    private CalendarHelper(Context context) {
        Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
                new String[]{"_id", "calendar_displayName", "calendar_timezone"},
                null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals("Testing")) {
                calendarId = cursor.getLong(0);
                timezone = cursor.getString(2);
                break;
            }
        }
        cursor.close();
    }

    public void loadSchedule(Context context, Schedule schedule) {
        String midnight = "" + schedule.getDate().getMidnight();
        String midnightTomorrow = "" + schedule.getDate().getMidnightTomorrow();
        Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                new String[]{"calendar_id", "_id", "title", "dtstart", "dtend"},
                "(calendar_id = ?) AND (dtstart > ?) AND (dtend < ?)",
                new String[]{"" + calendarId, midnight, midnightTomorrow}, null);
        ArrayList<TimeBlock> events = schedule.getSchedule();
        while (cursor.moveToNext()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getLong(3));
            DateTime start = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            calendar.setTimeInMillis(cursor.getLong(4));
            DateTime end = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            TimeBlock event = new TimeBlock(cursor.getLong(1), cursor.getString(2), start, end);
            events.add(event);
        }
        cursor.close();
    }

    public void saveSchedule(Context context, Schedule schedule) {
        new Handler(Looper.getMainLooper()).post(() -> {
            ArrayList<TimeBlock> events = schedule.getSchedule();
            for (TimeBlock event : events) {
                if (event.isChanged()) {
                    ContentValues values = new ContentValues();
                    values.put("title", event.getName());
                    values.put("dtstart", event.getStart().getEpochTime());
                    values.put("dtend", event.getEnd().getEpochTime());
                    values.put("eventTimezone", timezone);
                    values.put("calendar_id", calendarId);
                    if (event.getEventId() == -1) {
                        context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, values);
                    } else {
                        context.getContentResolver().update(
                                ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,
                                        event.getEventId()), values, null);
                    }
                }
                // TASK add block for if event was deleted
            }
            Toast.makeText(context, "Schedule saved", Toast.LENGTH_SHORT).show();
        });
    }

    public String getTimezone() {
        return timezone;
    }
}
