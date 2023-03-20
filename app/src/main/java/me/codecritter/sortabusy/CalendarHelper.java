package me.codecritter.sortabusy;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.TextView;

import java.util.Calendar;

public class CalendarHelper {

    private static CalendarHelper instance;

    public static CalendarHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CalendarHelper(context);
        }
        return instance;
    }

    private final CalendarSyncListener syncListener;
    private long calendarId;
    private Account account;
    private String timezone;


    private CalendarHelper(Context context) {
        Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,
                new String[]{"_id", "calendar_displayName", "account_name", "account_type",
                        "calendar_timezone"},
                null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(1).equals("Testing")) {
                calendarId = cursor.getLong(0);
                account = new Account(cursor.getString(2), cursor.getString(3));
                timezone = cursor.getString(4);
                break;
            }
        }
        cursor.close();

        syncListener = new CalendarSyncListener(context, account,
                CalendarContract.Calendars.CONTENT_URI.getAuthority());
        ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE
                | ContentResolver.SYNC_OBSERVER_TYPE_PENDING, syncListener);
    }

    public void debugMethod(Context context, TextView display) {
        Calendar start = Calendar.getInstance();
        start.set(2023, 2, 19, 20, 0);
        Calendar end = Calendar.getInstance();
        end.set(2023, 2, 19, 21, 0);

        ContentValues event = new ContentValues();
        event.put("title", "Testing");
        event.put("dtstart", start.getTimeInMillis());
        event.put("dtend", end.getTimeInMillis());
        event.put("eventTimezone", timezone);
        event.put("calendar_id", calendarId);
        context.getContentResolver().insert(CalendarContract.Events.CONTENT_URI, event);

        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account,
                CalendarContract.Calendars.CONTENT_URI.getAuthority(), extras);
    }
}
