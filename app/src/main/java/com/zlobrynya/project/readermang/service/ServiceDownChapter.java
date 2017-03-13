package com.zlobrynya.project.readermang.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StatFs;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.Activity.ShowDownloaded;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseDownloadMang;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.cacheImage.CacheFile;

import org.jsoup.Connection;
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
    private ArrayList<String> urlPage;
    private ArrayList<String> urlChapter;
    private ArrayList<String> listNameDir;
    private ArrayList<String> listNameChapter;
    private int numberPage;
    private int numberChapters;
    private int startID;
    private ArrayList<String> nameMang;
    private CacheFile cacheFile;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String path;
    private String urlSite;
    private boolean vibration,notif,sound;
    private final long thresholdStorage = 41943040; //40 mb


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
                //Растчет оставшегося места
                StatFs stat = new StatFs(path);
                long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getAvailableBlocks();
                if (bytesAvailable < thresholdStorage){
                    endNotif("Ошибка. Мало памяти для загрузки глав.");
                    stopSelf();
                    return;
                }

                startID++;
                numberChapters++;
                if (numberChapters < urlChapter.size()){
                    numberPage = 0;
//                    sendNotif();
                    urlPage.clear();
                    choiceParser();
                }else {
                    endNotif(getString(R.string.download_complite));
                    stopSelf();
                    Log.d(LOG_TAG, "stopSelf");
                }
            }
        }

        @Override
        public void onEnd(int number) {

        }
    };

    void cleaningDatabase(){
        int number = 0;
        for (int j = 0; j < nameMang.size();j++){
            String nameDirs = "";
            String nameChapters = "";
            String locName = nameMang.get(j).split(" ")[0];
            for (; number < numberChapters;number++){
                if (listNameChapter.get(number).contains(locName)){
                    String nameDir = listNameDir.get(number);
                    nameDirs = nameDir + "," + nameDirs;
                    String nameChapter = listNameChapter.get(number);
                    nameChapters = nameChapter.replace(",","").replace("новое","") + "," + nameChapters;
                }else {
                    break;
                }
            }
            ClassDataBaseDownloadMang classDataBaseDownloadMang = new ClassDataBaseDownloadMang(this);
            nameDirs = nameDirs + classDataBaseDownloadMang.getDataFromDataBase(nameMang.get(j),ClassDataBaseDownloadMang.NAME_DIR);
            classDataBaseDownloadMang.setData(nameMang.get(j),nameDirs,ClassDataBaseDownloadMang.NAME_DIR);
            nameChapters = nameChapters + classDataBaseDownloadMang.getDataFromDataBase(nameMang.get(j),ClassDataBaseDownloadMang.NAME_CHAPTER);
            classDataBaseDownloadMang.setData(nameMang.get(j),nameChapters,ClassDataBaseDownloadMang.NAME_CHAPTER);
        }
    }

    void endNotif(String msg){
        mBuilder.setNumber(startID)
                .setContentText(msg);
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
        mBuilder.setNumber(startID)
                .setContentText(listNameChapter.get(numberChapters))
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
        listNameDir = new ArrayList<>();
        listNameChapter = new ArrayList<>();
        cacheFile = new CacheFile();
        numberPage = 0;
        startID = 0;
        numberChapters = 0;
        nameMang = new ArrayList<>();
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
            urlSite = intent.getStringExtra("url_site");
            String[] chapter = intent.getStringExtra("chapter").split(",");
            String[] nameDir = intent.getStringExtra("name_dir").split(",");
            String[] nameChapters = intent.getStringExtra("chapter_name").split(",");

            nameMang.add(intent.getStringExtra("name_mang"));
            path = intent.getStringExtra("path");
            notif = intent.getBooleanExtra("notification",false);
            vibration = intent.getBooleanExtra("vibratyon",false);
            sound = intent.getBooleanExtra("sound",false);
            boolean firstUrl = false;
            if (urlChapter.isEmpty())
                firstUrl = true;

            for (String s: chapter)
                urlChapter.add(urlSite+s);

            Collections.addAll(listNameDir, nameDir);
            Collections.addAll(listNameChapter, nameChapters);

            if (firstUrl)
                choiceParser();

            Log.i(LOG_TAG, "d");
        }catch (NullPointerException ignored){

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void choiceParser(){
        if (urlChapter.get(numberChapters).contains("chan")){
            new ParsURLPageMC(receivedAddress).execute();
        }else {
            new ParsURLPageRM(receivedAddress).execute();
        }
    }

    public void onDestroy() {
        cleaningDatabase();
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //поток для скачивания сылок для изображений
    public class ParsURLPageRM extends AsyncTask<Void,Void,Void> {
        private Document doc;
        private AsyncTaskLisen asyncTask;
        private String html,nameChapter;
        private boolean not_net;

        //конструктор потока
        ParsURLPageRM(AsyncTaskLisen addImg) {
            asyncTask = addImg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            cacheFile.parameterSettingDownloadChapter(new File(path), listNameDir.get(numberChapters), asyncTask);
            try {
                Log.d(LOG_TAG, "Start Dow URL");
                //Запрос на получение сылок для изображений:
                if (doc == null) doc = Jsoup.connect(urlChapter.get(numberChapters)).get();

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
                asyncTask.onEnd();
            }
        }
    }

    //поток для скачивания сылок для изображений
    public class ParsURLPageMC extends AsyncTask<Void,Void,Void> {
        private Document doc;
        private AsyncTaskLisen asyncTask;
        private String html,nameChapter;
        private String errorMassage;
        private boolean not_net;

        //конструктор потока
        ParsURLPageMC(AsyncTaskLisen addImg) {
            asyncTask = addImg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            cacheFile.parameterSettingDownloadChapter(new File(path), listNameDir.get(numberChapters), asyncTask);
            try {
                if (doc == null) {
                    Connection.Response response = Jsoup.connect(urlChapter.get(numberChapters))
                            ///5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.2
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                            .timeout(100000)
                            .ignoreHttpErrors(true)
                            .execute();
                    int statusCode = response.statusCode();
                    if (statusCode == 200) {
                        doc = Jsoup.connect(urlChapter.get(numberChapters))
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .timeout(100000)
                                .get();
                    } else {
                        errorMassage = response.statusMessage() + " : " + response.statusCode();
                        not_net = true;
                        return null;
                    }
                    //

                    Elements scripts = doc.select("script");
                    for (Element script : scripts){
                        if (script.data().contains("var_news_id ")){
                            html = script.data();
                            int index = html.indexOf("fullimg");
                            html = html.substring(index);
                         //   Log.i("length", String.valueOf(html.length()));
                            index = html.indexOf("\n");
                            html = html.substring(10,index-2);
                         //   Log.i("length", String.valueOf(html.length()));
                            break;
                        }
                    }
                    // Log.i("JSON",html);
                    String[] url = html.split(",");
                    for (String anUrl : url) {
                        urlPage.add(anUrl.replace("\"","").replace("\"",""));
                    }
                    nameChapter = "";
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
            try{
                if (!not_net){
                    asyncTask.onEnd();
                }else{
                    //Toast.makeText(context, errorMassage, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e ){
                Crashlytics.logException(e);
            }
        }
    }
}
