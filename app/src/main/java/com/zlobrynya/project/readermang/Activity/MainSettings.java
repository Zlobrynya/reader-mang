package com.zlobrynya.project.readermang.Activity;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseDownloadMang;
import com.zlobrynya.project.readermang.Dialog.DialogPath;
import com.zlobrynya.project.readermang.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainSettings extends BaseActivity implements DialogPath.NoticeDialogListener  {
    private TextView path;

    private SharedPreferences.Editor editor;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.setting_main_fragment, frameLayout);

        SharedPreferences mSettings = getSharedPreferences(TopManga.APP_SETTINGS, Context.MODE_PRIVATE);
        editor = mSettings.edit();

        path = (TextView) findViewById(R.id.textSettingSystemDirectoryPath);

        path.setText(mSettings.getString(TopManga.APP_SETTINGS_PATH, getFilesDir().getAbsolutePath()));

       /* Switch WiFi = (Switch) findViewById(R.id.settings_wifi);
        WiFi.setChecked(mSettings.getBoolean(TopManga.APP_SETTINGS_WIFI, true));*/
        Switch NotificationAll = (Switch) findViewById(R.id.switch_all_notification);
        NotificationAll.setChecked((mSettings.getBoolean(TopManga.APP_SETTINGS_NOTIFICATION_DOWNLOAD_COMPLITE, true)));
        Switch vibration = (Switch) findViewById(R.id.switch_download_vibration);
        vibration.setChecked((mSettings.getBoolean(TopManga.APP_SETTINGS_NOTIFICATION_VIBRATION, true)));
        Switch soung = (Switch) findViewById(R.id.switch_download_soung);
        soung.setChecked((mSettings.getBoolean(TopManga.APP_SETTINGS_NOTIFICATION_SOUNG, true)));
        Switch chapterNew = (Switch) findViewById(R.id.switch_chapter_new);
        chapterNew.setChecked((mSettings.getBoolean(TopManga.APP_SETTINGS_NOTIFICATION_NEW_CHAPTER, true)));
        Switch saveZoom = (Switch) findViewById(R.id.switch_save_zoom);
        saveZoom.setChecked((mSettings.getBoolean(TopManga.APP_SETTINGS_SAVE_ZOOM, true)));
    }

    private void setSize(){
        File internalPath = new File(String.valueOf(path.getText()));
      /*  StatFs stat = new StatFs(internalPath.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();*/
        TextView infoSize = (TextView) findViewById(R.id.text_setting_system_clear_size);
        infoSize.setText(Formatter.formatFileSize(this, internalPath.getTotalSpace()));
    }

    @Override
    public void onDialogClickPath(String path) {
        AsyncMoveFile Task = new AsyncMoveFile();
        Task.execute(String.valueOf(this.path.getText()), path);
        Toast.makeText(this, "Перенос файлов", Toast.LENGTH_SHORT).show();
        this.path.setText(path);
        editor.putString(TopManga.APP_SETTINGS_PATH, path);
        editor.commit();
      //  setSize();
    }

    @Override
    public void onDialogChoiceOfDirectory(DialogFragment dialog) {

    }

    public void NotificationNewChapter(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(TopManga.APP_SETTINGS_NOTIFICATION_NEW_CHAPTER,aSwitch.isChecked());
        editor.commit();
    }

    public void NotificationVibration(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(TopManga.APP_SETTINGS_NOTIFICATION_VIBRATION,aSwitch.isChecked());
        editor.commit();
    }

    public void NotificationSoung(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(TopManga.APP_SETTINGS_NOTIFICATION_SOUNG,aSwitch.isChecked());
        editor.commit();
    }

    public void AllNotification(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(TopManga.APP_SETTINGS_NOTIFICATION_DOWNLOAD_COMPLITE,aSwitch.isChecked());
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
            try {
                for(File chapter: chapters){
                    File imgs[] = chapter.listFiles();
                    try {
                        for(File img: imgs) {
                            img.delete();
                        }
                    }catch (NullPointerException e){
                        // Log.i("Settings",e.getMessage());
                    }
                    chapter.delete();
                }
            }catch (NullPointerException ignored){

            }
            dir.delete();
        }
        ClassDataBaseDownloadMang downloadMang = new ClassDataBaseDownloadMang(this);
        downloadMang.clearAll();
        Toast.makeText(this, "Очистка загруженной манги завершена.", Toast.LENGTH_SHORT).show();
    }

    public void selectionMangaSite(View view) {
        startActivity(new Intent(MainSettings.this,SelectionMangSite.class));
    }

    public void SystemSaveZoom(View view) {
        Switch aSwitch = (Switch) view;
        editor.putBoolean(TopManga.APP_SETTINGS_SAVE_ZOOM,aSwitch.isChecked());
        editor.commit();
    }

    //Перенос изображений
    public class AsyncMoveFile extends AsyncTask<String,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        //params[0] - output, params[1]-input
        protected Void doInBackground(String... params) {
            File internalPath = new File(params[0]);
            File[] dirs= internalPath.listFiles();
            for(File dir: dirs){
                File newDir = new File(params[1]+"/"+dir.getName());
                if (!newDir.exists()){
                    newDir.mkdir();
                }
                File chapters[] = dir.listFiles();
                for(File chapter: chapters){
                    File newChapter = new File(params[1]+"/"+dir.getName()+"/"+chapter.getName());
                    if (!newChapter.exists()){
                        newChapter.mkdir();
                    }
                    File imgs[] = chapter.listFiles();
                    try{
                        for(File img: imgs) {
                            moveFile(img.getPath(),newChapter.getPath()+"/"+img.getName());
                            img.delete();
                        }
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                    chapter.delete();
                }
                dir.delete();
            }
            return null;
        }

        private void moveFile(String inputPath, String outputPath) {
            InputStream in = null;
            OutputStream out = null;
            try {

                in = new FileInputStream(inputPath);
                out = new FileOutputStream(outputPath);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                in = null;

                // write the output file
                out.flush();
                out.close();
                out = null;
            }
            catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void result){

        }
    }
}
