package com.example.nikita.progectmangaread.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.cacheImage.CacheFile;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;


public class ServiceDownChapter extends Service {
    private final String LOG_TAG = "Servise Down";
    private ArrayList<String> urlPage,urlChapter,listNameMang,listNameChapter;
    private int numberPage,startId;
    private CacheFile cacheFile;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String path;


    AsyncTaskLisen receivedAddress = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            Log.d(LOG_TAG, "Start Dow Pic");
            if (numberPage < urlPage.size()){
                cacheFile.loadAndCache(urlPage.get(numberPage), String.valueOf(numberPage));
                numberPage++;
            }else {
                startId++;
                if (startId < urlChapter.size()){
                    numberPage = 0;
                    sendNotif();
                    urlPage.clear();
                    new ParsURLPage(receivedAddress).execute();
                }else {
                    sendNotif();
                    stopSelf();
                    Log.d(LOG_TAG, "stopSelf");
                }
            }
        }

        @Override
        public void onEnd(InputStream is) {

        }
    };

    void sendNotif() {
       mBuilder.setContentText("Chapter downloaded: ")
               .setNumber(startId);
        // то что ниже для расширенного отображения (несколько строк подряд с названиеми глав которые скачались)
       /*  NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
        for (int i=0; i < listNameChapter.size(); i++) {
            inboxStyle.addLine(listNameChapter.get(i));
        }*/

        // Moves the expanded layout object into the notification object.
        //mBuilder.setStyle(inboxStyle);

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
                .setContentTitle("Downloaded")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.launcher);

        Log.d(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String urlSite = intent.getStringExtra("url_site");
        String[] chapter = intent.getStringExtra("chapter").split(",");
        String[] nameDir = intent.getStringExtra("name_dir").split(",");
        path = intent.getStringExtra("path");

        boolean firstUrl = false;
        if (urlChapter.isEmpty())
            firstUrl = true;

        for (String s: chapter)
            urlChapter.add(urlSite+s);
        Collections.addAll(listNameMang, nameDir);

        if (firstUrl)
            new ParsURLPage(receivedAddress).execute();

        Log.i(LOG_TAG, "d");
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
        protected ParsURLPage(AsyncTaskLisen addImg) {
            asyncTask = addImg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            cacheFile.parameterSetting(new File(path),listNameMang.get(startId),receivedAddress,null);
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
