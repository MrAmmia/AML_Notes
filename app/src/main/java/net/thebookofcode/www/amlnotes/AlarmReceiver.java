package net.thebookofcode.www.amlnotes;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        Notification.Builder nb = null;
        nb = notificationHelper.getNotification1("AML Notes Alarm","Your Alarm is ringing");
        if (nb != null) {
            notificationHelper.notify(1, nb);
        }
    }
}
