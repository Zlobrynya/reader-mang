package com.example.nikita.progectmangaread.Activity;


import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseDownloadMang;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.service.MoveFile;

import java.io.File;

public class MainSettings extends BaseActivity implements DialogPath.NoticeDialogListener  {
    private TextView time,path;
    public static final String APP_SETTINGS = "globalSettings";
    public static final String APP_SETTINGS_WIFI = "WiFi";
    public static final String APP_SETTINGS_PATH = "path";
    public static final String APP_SETTINGS_NOTIFICATION_DOWNLOAD_COMPLITE = "downloadComp";
    public static final String APP_SETTINGS_NOTIFICATION_VIBRATION = "vibration";
    public static final String APP_SETTINGS_NOTIFICATION_SOUNG = "soung";
    public static final String APP_SETTINGS_NOTIFICATION_NEW_CHAPTER = "notificationNewChapter";
    public static final String APP_SETTINGS_NOTIFICATION_TIME_DOWNLOAD = "notificationTime";
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.setting_main_fragment, frameLayout);

        SharedPreferences mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        editor = mSettings.edit();

        time = (TextView) findViewById(R.id.textNotificationTimeUpdate);
        path = (TextView) findViewById(R.id.textSettingSystemDirectoryPath);

        time.setText(mSettings.getString(APP_SETTINGS_NOTIFICATION_TIME_DOWNLOAD, "10:10"));
        path.setText(mSettings.getString(APP_SETTINGS_PATH, getFilesDir().getAbsolutePath()));

        Switch WiFi = (Switch) findViewById(R.id.settings_wifi);
        WiFi.setChecked(mSettings.getBoolean(APP_SETTINGS_WIFI, false));
        Switch NotificationAll = (Switch) findViewById(R.id.switch_all_notification);
        NotificationAll.setChecked((mSettings.getBoolean(APP_SETTINGS_NOTIFICATION_DOWNLOAD_COMPLITE, false)));
     //   Switch ChapterComplite = (Switch) findViewById(R.id.switch_chapter_complite);
    //    ChapterComplite.setClickable((mSettings.getBoolean(APP_SETTINGS_NOTIFICATION_DOWNLOAD_COMPLITE, false)));
        Switch vibration = (Switch) findViewById(R.id.switch_download_vibration);
        vibration.setChecked((mSettings.getBoolean(APP_SETTINGS_NOTIFICATION_VIBRATION, false)));
        Switch soung = (Switch) findViewById(R.id.switch_download_soung);
        soung.setChecked((mSettings.getBoolean(APP_SETTINGS_NOTIFICATION_SOUNG, false)));
        Switch chapterNew = (Switch) findViewById(R.id.switch_chapter_new);
        chapterNew.setChecked((mSettings.getBoolean(APP_SETTINGS_NOTIFICATION_NEW_CHAPTER, false)));

     //   setSize();
    }

    private void setSize(){
        File internalPath = new File(String.valueOf(path.getText()));
        StatFs stat = new StatFs(internalPath.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();
        TextView infoSize = (TextView) findViewById(R.id.text_setting_system_clear_size);
        infoSize.setText(Formatter.formatFileSize(this, internalPath.getTotalSpace()));
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            //Тут обработка данных времени с TimePickerDialog
           String min;
            if (minute < 10)
                min = "0"+minute;
            else
                min = String.valueOf(minute);
            time.setText(hourOfDay+":"+min);
            editor.putString(APP_SETTINGS_NOTIFICATION_TIME_DOWNLOAD, hourOfDay + ":" + min);
            editor.commit();
        }
    };

    @Override
    public void onDialogClickPath(String path) {
        startService(new Intent(MainSettings.this, MoveFile.class).putExtra("out", this.path.getText())
                .putExtra("inp", path));
        Toast.makeText(this, "Перенос файлов", Toast.LENGTH_SHORT).show();
        this.path.setText(path);
        editor.putString(APP_SETTINGS_PATH, path);
        editor.commit();
      //  setSize();
    }

    @Override
    public void onDialogChoiceOfDirectory(DialogFragment dialog) {

    }

    public void NotificationTime(View view) {
        new TimePickerDialog(MainSettings.this, myCallBack, 10, 10, true).show();
    }

    public void InfoTime(View view) {
        DialogInfo dialogInfoTime = new DialogInfo();
        dialogInfoTime.show(getFragmentManager(), "Time");
    }

    public void NotificationNewChapter(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(APP_SETTINGS_NOTIFICATION_NEW_CHAPTER,aSwitch.isChecked());
        editor.commit();
    }

    public void NotificationVibration(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(APP_SETTINGS_NOTIFICATION_VIBRATION,aSwitch.isChecked());
        editor.commit();
    }

    public void NotificationSoung(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(APP_SETTINGS_NOTIFICATION_SOUNG,aSwitch.isChecked());
        editor.commit();
    }

    public void AllNotification(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(APP_SETTINGS_NOTIFICATION_DOWNLOAD_COMPLITE,aSwitch.isChecked());
        editor.commit();
    }

    public void WiFi(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(APP_SETTINGS_WIFI,aSwitch.isChecked());
        editor.commit();
    }

    public void PathDownload(View view) {
        DialogPath dialogPath = new DialogPath();
        dialogPath.show(getFragmentManager(), "Path");
    }

    public void clickInfoPath(View view) {
    }

    public void ClearAll(View view) {
        File[] dirs= new File((String) path.getText()).listFiles();
        for(File dir: dirs){
            File chapters[] = dir.listFiles();
            for(File chapter: chapters){
                File imgs[] = chapter.listFiles();
                for(File img: imgs) {
                    img.delete();
                }
                chapter.delete();
            }
            dir.delete();
        }
        ClassDataBaseDownloadMang downloadMang = new ClassDataBaseDownloadMang(this);
        downloadMang.clearAll();
    }
}
