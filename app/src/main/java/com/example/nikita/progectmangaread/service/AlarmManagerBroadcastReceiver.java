package com.example.nikita.progectmangaread.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nikita on 27.07.2016.
 */
public class AlarmManagerBroadcastReceiver extends WakefulBroadcastReceiver {

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
        //Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();

        context.startService(new Intent(context.getApplicationContext(), UpdateMangBookmark.class).putExtra("Data", msgStr));
        //Release the lock
        wl.release();
    }


    public void SetAlarm(Context context,String data)
    {
        AlarmManager am= (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        //раз в 3 часа AlarmManager.INTERVAL_HOUR*3
        if(Build.VERSION.SDK_INT < 23){
            am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR*3, pi);
        }
        else{
            //am.setExactAndAllowWhileIdle(AlarmManager.RTC,150000,pi);
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR*3 ,pi);
        }

    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}