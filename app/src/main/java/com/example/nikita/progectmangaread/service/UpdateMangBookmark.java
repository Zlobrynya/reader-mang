package com.example.nikita.progectmangaread.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class UpdateMangBookmark extends Service {
    private ArrayList<String> nameMang;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private Helper helper;
    private ExecutorService es;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private int numberUpdate;
    private String data;
    private boolean firstUpdate;

    class Helper{
        public ArrayList<String> nameBookmark;
        public ArrayList<String> siteBookmark;

        Helper(){
            nameBookmark = new ArrayList<>();
            siteBookmark = new ArrayList<>();
        }
    }

    private AsyncTaskLisen asyncTask = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            while (!nameMang.isEmpty()){
                String s = nameMang.get(0);
                for (String name: helper.nameBookmark){
                    if (s.split(" ")[0].contains(name)){
                        numberUpdate++;
                    }
                }
                nameMang.remove(0);
            }
            if (firstUpdate) {
                new ParsUpdateMang(asyncTask,"http://mintmanga.com/news/calendar/day/" +data).execute();
                firstUpdate = false;
            }else {
                sendNotif();
            }
        }

        @Override
        public void onEnd(InputStream is) {

        }
    };

    void sendNotif() {
        mBuilder.setContentText("Chapter downloaded: ")
                .setNumber(numberUpdate);
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
        nameMang = new ArrayList<>();
        helper = new Helper();
        numberUpdate = 0;
        firstUpdate = true;
        //инициализация уведомлений
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Downloaded")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.drawable.ic_launcher);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        ClassDataBaseViewedHead classDataBaseViewedHead = new ClassDataBaseViewedHead(getApplicationContext());
        Cursor cursor = classDataBaseViewedHead.getNotebook();
       /* for (int i = 0; i < cursor.getCount();i++){
            helper.nameBookmark.add(cursor.getString(cursor.getColumnIndex(ClassDataBaseViewedHead.NAME_LAST_CHAPTER)));
            if (cursor.getString(cursor.getColumnIndex(ClassDataBaseViewedHead.LAST_CHAPTER)).contains("readmang"))
                helper.siteBookmark.add("readmang");
            else
                helper.siteBookmark.add("mintmanga");
            cursor.moveToNext();
        }*/
        cursor.moveToFirst();
        for(int i = 0;i < cursor.getCount();i++){
            helper.nameBookmark.add(cursor.getString(cursor.getColumnIndex(ClassDataBaseViewedHead.NAME_LAST_CHAPTER)).split(" ")[0]);
            if (cursor.getString(cursor.getColumnIndex(ClassDataBaseViewedHead.LAST_CHAPTER)).contains("readmang"))
                helper.siteBookmark.add("readmang");
            else
                helper.siteBookmark.add("mintmanga");
            cursor.moveToNext();
        }
        cursor.close();
        classDataBaseViewedHead.closeDataBase();

        data = intent.getStringExtra("Data");
        new ParsUpdateMang(asyncTask,"http://readmanga.me/news/calendar/day/"+data).execute();
        return super.onStartCommand(intent, flags, startId);
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
