package com.example.nikita.progectmangaread.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Nikita on 27.07.2016.
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    private final String LOG_TAG = "AlarmManager";

    //http://readmanga.me/news/calendar/day/27-07-2016

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();


        String msgStr = "";
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        msgStr = formatter.format(new Date());
        Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

        context.startService(new Intent(context.getApplicationContext(), UpdateMangBookmark.class).putExtra("Data", msgStr));
        //Release the lock
        wl.release();
    }


    public void SetAlarm(Context context,String data)
    {
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        //HOUR_OF_DAY - 24 часа / HOUR - 12 часов
        calendar.set(Calendar.HOUR, 13);
        calendar.set(Calendar.MINUTE, 30);

        //After after 1000*60*24 = 24 часа
        am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000*60*24, pi);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }



}