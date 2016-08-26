package com.example.nikita.progectmangaread.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.example.nikita.progectmangaread.Activity.Bookmark;
import com.example.nikita.progectmangaread.Activity.DownloadChapter;
import com.example.nikita.progectmangaread.Activity.MainSettings;
import com.example.nikita.progectmangaread.Activity.ShowDownloaded;
import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseListMang;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UpdateMangBookmark extends Service {
    private ArrayList<String> nameMang,nameMangUpdate;
    private ArrayList<Integer> numberOfMang;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String newData,lastData;
    private int numberSite;
    private HashMap<Integer,ArrayList<String>> nameBookmark;
    private SharedPreferences mSettings;

    //получаем имя манги до цифры тома
    private String getNameMang(String name){
        String nameMang = "";
        int number = 0;
        String[] names = name.split(" ");
        for (String nameUp: names) {
            try {
                number++;
                int num = Integer.parseInt(nameUp);
                if (number == 1)
                    nameMang += nameUp+" ";
                else
                    break;
            } catch (NumberFormatException e) {
                if (!nameUp.equals("Манга"))
                    nameMang += nameUp+" ";

            }
        }
        return nameMang;
    }

    //сравнение двух названий манг
    private boolean stringComparison(String name1,String name2){
        if (name1.split(" ").length > 3 && name2.split(" ").length > 3){
            String[] names1 = name1.split(" ");
            String[] names2 = name2.split(" ");
            String locName1 = names1[0]+" "+names1[1];
            boolean result = locName1.equals((names2[0] + " " + names2[1]));
            if (result) Log.i("fd","fd");
            return result;
        }else{
            return name1.equals(name2);
        }
    }

    private AsyncTaskLisen asyncTask = new AsyncTaskLisen() {
        @Override
        public void onEnd() {

            // for (int i = 0; i < (nameMang.size()-numberOfMang.get(numberSite)); i++){
            //numberOfMang.get(numberSite)
            for (int i = 0; i < (nameMang.size()-numberOfMang.get(numberSite)); i++){
                String s = nameMang.get(i);
                for (int k = 0; k < nameBookmark.get(numberSite).size(); k++){
                    String name = nameBookmark.get(numberSite).get(k);
                    s = getNameMang(s);
                    name = getNameMang(name);
                    if (stringComparison(s,name)){
                        if (nameMangUpdate.isEmpty()){
                            nameMangUpdate.add(s);
                        }else {
                            boolean doesNot = true;
                            for (int j = 0; j < nameMangUpdate.size();j++){
                                if (stringComparison(s,nameMangUpdate.get(j))){
                                    doesNot = false;
                                }
                            }
                            if (doesNot)
                                nameMangUpdate.add(s);
                        }
                    }
                }
            }

            if (numberSite == 0)
                numberOfMang.set(0, nameMang.size());
            else numberOfMang.set(1,nameMang.size());

            if (numberSite == 0) {
                nameMang.clear();
                if (!nameBookmark.get(1).isEmpty())
                    new ParsUpdateMang(asyncTask,"http://mintmanga.com/news/calendar/day/" + newData).execute();
                else {
                    if (!nameMangUpdate.isEmpty())
                        sendNotif();
                }
                numberSite = 1;
            }else {
                if (!nameMangUpdate.isEmpty())
                    sendNotif();
            }
        }

        @Override
        public void onEnd(int number) {

        }
    };

    void sendNotif() {
        // то что ниже для расширенного отображения (несколько строк подряд с названиеми глав которые скачались)
         NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Manga update:");
        for (String name: nameMangUpdate)
            inboxStyle.addLine(name);

        // Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        int notifyID = 1;
        mNotificationManager.notify(
                notifyID,
                mBuilder.build());
    }

    public void onCreate() {
        super.onCreate();
        nameMang = new ArrayList<>();
        nameMangUpdate = new ArrayList<>();
        mSettings = getSharedPreferences(MainSettings.APP_SETTINGS, MODE_PRIVATE);

        numberOfMang = new ArrayList<>();
        numberOfMang.add(0);
        numberOfMang.add(0);

        nameBookmark = new HashMap<>();
        lastData = "";
        numberSite = 0;
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(Bookmark.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(new Intent(this, Bookmark.class));
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //инициализация уведомлений
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Manga update")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(resultPendingIntent);;

    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("service","onStartCommand");
        if (!nameMang.isEmpty())
            nameMang.clear();

        if (!nameMangUpdate.isEmpty())
            nameMangUpdate.clear();

        numberSite = 0;

        ClassDataBaseViewedHead classDataBaseViewedHead = new ClassDataBaseViewedHead(getApplicationContext());
        Cursor cursor = classDataBaseViewedHead.getNotebook();
        if (cursor != null){
            cursor.moveToFirst();
            ArrayList<String> bookmarkReadManga = new ArrayList<>();
            ArrayList<String> bookmarkMintManga = new ArrayList<>();
            //получение списка закладок
            for(int i = 0;i < cursor.getCount();i++){
                String bookmark = cursor.getString(cursor.getColumnIndex(ClassDataBaseViewedHead.NAME_LAST_CHAPTER));
                if (cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.URL_CHAPTER)).contains("readmang"))
                    bookmarkReadManga.add(bookmark);
                else
                    bookmarkMintManga.add(bookmark);
                cursor.moveToNext();
            }
            cursor.close();
            classDataBaseViewedHead.closeDataBase();
            nameBookmark.put(0, bookmarkReadManga);
            nameBookmark.put(1, bookmarkMintManga);

            try {
                newData = intent.getStringExtra("Data");

                //Сбрасываем счетчик количества опубликованых манг, когда проходить день
                if (!lastData.isEmpty()){
                    DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = format.parse(newData);
                    if (date.after(format.parse(lastData))){
                        numberOfMang.set(0,0);
                        numberOfMang.set(1,0);
                    }
                }
                lastData = newData;
                boolean wifi = mSettings.getBoolean(MainSettings.APP_SETTINGS_WIFI,false);
                if (wifi){
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(UpdateMangBookmark.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (mWifi.isConnected()) {
                        startPars();
                    }
                }else {
                    startPars();
                }
            }catch (NullPointerException | ParseException e){
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startPars(){
        //Для запуска парсера если есть закладки.
        for (int i = 0; i < nameBookmark.size();i++){
            //метка для выхода, может позже узнаю как можно сделать более красивее
            boolean repeat = false;
            switch (i){
                case 0:
                    new ParsUpdateMang(asyncTask,"http://readmanga.me/news/calendar/day/"+ newData).execute();
                    break;
                case 1:
                    new ParsUpdateMang(asyncTask,"http://mintmanga.com/news/calendar/day/" + newData).execute();
                    numberSite = 1;
                    break;
                default:
                    repeat = true;
            }
            if (!repeat)
                break;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //поток для обновления
    private class ParsUpdateMang extends AsyncTask<Void,Void,Void> {
        private Document doc;
        private AsyncTaskLisen asyncTask;
        private String URL;
        private boolean not_net;

        //конструктор потока
        protected ParsUpdateMang(AsyncTaskLisen addImg,String URL) {
            asyncTask = addImg;
            this.URL = URL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //Запрос на получение сылок для изображений:
                if (doc == null) doc = Jsoup.connect(URL).get();

                Elements table = doc.select("[class=table table-hover newChapters]").select("tr");
                for (Element element : table){
                    nameMang.add(element.select("a").text());
                }
            } catch (IOException e) {
                e.printStackTrace();
                not_net = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("serviseUpdate","Не грузит страницу либо больше нечего грузить");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            if (!not_net){
                asyncTask.onEnd();
            }
        }
    }
}
