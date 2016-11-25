package com.zlobrynya.project.ereadermanga.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.zlobrynya.project.ereadermanga.Activity.ShowDownloaded;
import com.zlobrynya.project.ereadermanga.AsyncTaskLisen;
import com.zlobrynya.project.ereadermanga.DataBasePMR.ClassDataBaseDownloadMang;
import com.zlobrynya.project.ereadermanga.R;
import com.zlobrynya.project.ereadermanga.cacheImage.CacheFile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class ServiceDownChapter extends Service {
    private final String LOG_TAG = "Servise Down";
    private ArrayList<String> urlPage,urlChapter,listNameMang,listNameChapter;
    private int numberPage,startId;
    private CacheFile cacheFile;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String path;
    private boolean vibration,notif,sound;
    private ClassDataBaseDownloadMang classDataBaseDownloadMang;

    AsyncTaskLisen receivedAddress = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            Log.d(LOG_TAG, "Start Dow Pic");
            if (numberPage < urlPage.size()){
                if (notif)
                    sendNotif();
                cacheFile.loadAndCache(urlPage.get(numberPage), String.valueOf(numberPage));
                numberPage++;
            }else {
                startId++;
                if (startId < urlChapter.size()){
                    numberPage = 0;
//                    sendNotif();
                    urlPage.clear();
                    new ParsURLPage(receivedAddress).execute();
                }else {
                    endNotif();
                    stopSelf();
                    Log.d(LOG_TAG, "stopSelf");
                }
               /* if (vibration){
                    // Get instance of Vibrator from current Context
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 300 milliseconds
                    v.vibrate(300);
                }*/
            }
        }

        @Override
        public void onEnd(int number) {

        }
    };

    void endNotif(){
        mBuilder.setNumber(startId)
                .setContentText(getString(R.string.download_complite));
        int notifyID = 1;

        if (numberPage == urlPage.size()-1){
            if (sound && vibration)
                mBuilder.setDefaults(Notification.DEFAULT_SOUND |
                        Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
            else if (sound)
                mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
            else if (vibration)
                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        }else {
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        }

        mNotificationManager.notify(
                notifyID,
                mBuilder.build());
    }

    //Отправка уведомления
    void sendNotif() {
        mBuilder.setNumber(startId)
                .setContentText(listNameChapter.get(startId))
                .setProgress(urlPage.size() - 1, numberPage, false);
        int notifyID = 1;



        mNotificationManager.notify(
                notifyID,
                mBuilder.build());
    }

    public void onCreate() {
        super.onCreate();
        urlPage = new ArrayList<>();
        urlChapter = new ArrayList<>();
        listNameMang = new ArrayList<>();
        listNameChapter = new ArrayList<>();
        cacheFile = new CacheFile();
        numberPage = 0;
        startId = 0;

        //инициализация уведомлений
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Download")
                .setSmallIcon(R.drawable.ic_file_download_white_24dp);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(ShowDownloaded.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(new Intent(this, ShowDownloaded.class));

        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String urlSite = intent.getStringExtra("url_site");
            String[] chapter = intent.getStringExtra("chapter").split(",");
            String[] nameDir = intent.getStringExtra("name_dir").split(",");

            path = intent.getStringExtra("path");
            notif = intent.getBooleanExtra("notification",false);
            vibration = intent.getBooleanExtra("vibratyon",false);
            sound = intent.getBooleanExtra("sound",false);
            boolean firstUrl = false;
            if (urlChapter.isEmpty())
                firstUrl = true;

            for (String s: chapter)
                urlChapter.add(urlSite+s);
            Collections.addAll(listNameMang, nameDir);

            if (firstUrl)
                new ParsURLPage(receivedAddress).execute();

            Log.i(LOG_TAG, "d");
        }catch (NullPointerException ignored){
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //поток для скачивания сылок для изображений
    public class ParsURLPage extends AsyncTask<Void,Void,Void> {
        private Document doc;
        private AsyncTaskLisen asyncTask;
        private String html,nameChapter;
        private boolean not_net;

        //конструктор потока
        ParsURLPage(AsyncTaskLisen addImg) {
            asyncTask = addImg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            cacheFile.parameterSettingDownloadChapter(new File(path), listNameMang.get(startId), receivedAddress);
            try {
                Log.d(LOG_TAG, "Start Dow URL");
                //Запрос на получение сылок для изображений:
                if (doc == null) doc = Jsoup.connect(urlChapter.get(startId)).get();

                nameChapter = doc.select("[class = pageBlock container]").select("h1").text();

                Elements scripts = doc.select("body").select("script");
                for (Element script : scripts){
                    if (script.data().contains("transl_next_page")){
                        html = script.data();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                not_net = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("pageDowload","Не грузит страницу либо больше нечего грузить");
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result){
            if (!not_net){
                StringBuilder secondBuffer = new StringBuilder(html);
                String stringBuffer = "";
                stringBuffer = secondBuffer.substring(secondBuffer.indexOf("init") + 6, secondBuffer.lastIndexOf("false") - 4);
                stringBuffer = stringBuffer.replace("[","");
                stringBuffer = stringBuffer.replace("]","");
                String[] test = stringBuffer.split(",");

                String[] URLhelp;
                URLhelp = new String[3];
                int kol = 0;

                for(String tt: test){
                    if (tt.contains("'")){
                        URLhelp[kol] = tt.substring(tt.indexOf("'")+1,tt.lastIndexOf("'"));
                        kol++;
                    }else if (tt.contains("\"")){
                        URLhelp[2] = tt.substring(tt.indexOf("\"")+1,tt.lastIndexOf("\""));
                        kol++;
                    }
                    if (kol == 3){
                        kol = 0;
                        urlPage.add(URLhelp[1] + URLhelp[0] + URLhelp[2]);
                    }
                }
                listNameChapter.add(nameChapter);
                asyncTask.onEnd();
            }
        }
    }
}
