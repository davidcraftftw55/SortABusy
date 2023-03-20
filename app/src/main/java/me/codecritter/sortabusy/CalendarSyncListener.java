package me.codecritter.sortabusy;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.util.Log;

public class CalendarSyncListener implements SyncStatusObserver {

    private final Context context;
    private final Account account;
    private final String authority;

    public CalendarSyncListener(Context context, Account account, String authority) {
        this.context = context;
        this.account = account;
        this.authority = authority;
    }

    @Override
    public void onStatusChanged(int status) {
        if (status == ContentResolver.SYNC_OBSERVER_TYPE_PENDING) {
            if (ContentResolver.isSyncPending(account, authority)) {
                Log.i("log", "now pending");
            } else {
                Log.i("log", "no longer pending");
            }
        } else if (status == ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE) {
            if (ContentResolver.isSyncActive(account, authority)) {
                Log.i("log", "now active");
            } else {
                Log.i("log", "no longer active");
            }
        }
    }
}
