package com.entscz.krizovezasoby;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){

            SharedPreferences prefs = context.getSharedPreferences("login", Context.MODE_PRIVATE);

            String storedEmail = prefs.getString("email", null);
            String storedPassword = prefs.getString("password", null);

            if(storedEmail!=null && storedPassword!=null) {

                SharedPreferences notifPrefs = context.getSharedPreferences("notifications", Context.MODE_PRIVATE);

                if (notifPrefs.getBoolean("showNotifications", false)) {

                    Intent notificationHandlerIntent = new Intent(context, NotificationHandler.class);
                    PendingIntent existingAlarmIntent = PendingIntent.getBroadcast(context, 0, notificationHandlerIntent, PendingIntent.FLAG_NO_CREATE);

                    if (existingAlarmIntent == null) {

                        int notificationHours = notifPrefs.getInt("hours", 12);
                        int notificationMinutes = notifPrefs.getInt("minutes", 0);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, notificationHours);
                        calendar.set(Calendar.MINUTE, notificationMinutes);

                        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, notificationHandlerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

                        Toast.makeText(context, "Notification alarm set!", Toast.LENGTH_SHORT).show();

                    }

                }
            }

        }
    }
}