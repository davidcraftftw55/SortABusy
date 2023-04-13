package me.codecritter.sortabusy.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.CalendarContract;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;

import me.codecritter.sortabusy.DateTime;
import me.codecritter.sortabusy.Schedule;
import me.codecritter.sortabusy.TimeBlock;

/**
 * Utility class containing all this app's interactions with the CalendarContract, the phone's SQL database for calendar events
 */
public class CalendarHelper {

    private static CalendarHelper instance;

    /**
     * Gets the current instance of CalendarHelper, creating a new instance if there is none
     * @param context context needed (in case a new CalendarHelper is to be constructed
     * @return current or new instance of CalendarHelper
     */
    public static CalendarHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CalendarHelper(context);
        }
        return instance;
    }

    private final Handler saveHandler;
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

        saveHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Toast.makeText(context, "Schedule saved", Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * Loads all events into schedule object, the Schedule object's schedule ArrayList will be
     * populated with events that occur the Date specified in the Schedule object
     * @param context context needed
     * @param schedule schedule object to load events into
     */
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

    /**
     * Saves all events with changes in the specified Schedule parameter to the CalendarContract
     * (to which the phone will eventually save to GoogleCalendar)
     * @param context context needed
     * @param schedule schedule object to save to CalendarContract
     */
    public void saveSchedule(Context context, Schedule schedule) {
        saveHandler.post(() -> {
            ArrayList<TimeBlock> events = schedule.getSchedule();
            for (TimeBlock event : events) {
                if (event.isChanged() && !event.getName().isEmpty()) {
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
                                        event.getEventId()), values, null, null);
                    }
                    event.markAsUpdated();
                }
            }
            saveHandler.sendEmptyMessage(0);
        });
    }

    /**
     * Deletes event from CalendarContract (to which phone will remove from GoogleCalendar)
     * @param context context needed
     * @param event TimeBlock containing the attributes of the event to delete
     */
    public void deleteEvent(Context context, TimeBlock event) {
        context.getContentResolver().delete(ContentUris.withAppendedId(
                CalendarContract.Events.CONTENT_URI, event.getEventId()), null, null);
    }

    /**
     * Getter method of the timezone variable
     * @return String representation of this phone's timezone
     */
    public String getTimezone() {
        return timezone;
    }
}
