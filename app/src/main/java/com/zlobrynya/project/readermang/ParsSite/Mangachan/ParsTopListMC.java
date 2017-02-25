package com.zlobrynya.project.readermang.ParsSite.Mangachan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.ParsSite.ParsPageMang;
import com.zlobrynya.project.readermang.ParsSite.ParsTopList;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassMang;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Nikita on 14.02.2017.
 */

public class ParsTopListMC extends ParsTopList {
    private boolean endDownload = false;

    public ParsTopListMC(Context context, LinkedList<ClassMainTop> list) {
        super(context, list);
        page = 1;
    }

    public void parsSite(int kol){
        ParsTopList past = new ParsTopList(callback, kol, classMang);
        past.execute();
    }

    //Перенос возможен в отдельный пакет.
    private class ParsTopList extends AsyncTask<Void,Void,Void> {
        private String nameChar,URL2;
        private ClassMang classMang;
        private AsyncTaskLisen lisens;
        private String imgSrc;
        private int kol;
        private boolean not_net;
        private String errorMassage;

        //конструктор потока
        ParsTopList(AsyncTaskLisen callback, int kol, ClassMang classMang) {
            this.lisens = callback;
            this.kol = kol;
            this.classMang = classMang;
            URL2 = nameChar = imgSrc = "";
            errorMassage = "Ошибка подключения.";
           // this.classMang.setPath2("?offset=");
        }

        @Override
        protected  void  onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                int kol_mang;
                boolean nextPage = false;
                // Проверка на то что сейчас парсим, если запрос то там без страниц идет
                page = (kol / classMang.getMaxInPage());
                kol_mang = kol - (classMang.getMaxInPage()*page);

                if (doc == null){
                    String url = "";
                    if (resultPost == 1){
                        url = getWhere(page+1);
                        nextPage = true;
                    }
                    else url = getWhere(kol);
                    Log.i("Where",classMang.getURL() + url);

                    Connection.Response response = Jsoup.connect(classMang.getURL() + url)
                            ///5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.2
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                            .timeout(100000)
                            .ignoreHttpErrors(true)
                            .execute();
                    int statusCode = response.statusCode();
                    if (statusCode == 200){
                        doc = Jsoup.connect(classMang.getURL() + url)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .timeout(100000)
                                .get();
                    }else {
                        errorMassage = response.statusMessage() + " : " + response.statusCode();
                        not_net = true;
                        return null;
                    }
                }

                Element el = doc.select(classMang.getNameCell()).first();
                for (int i = 0; i < kol_mang; i++)
                    el = el.nextElementSibling();
                Elements el2 = el.select(classMang.getNameURL());
                //Log.i("kol_mang", String.valueOf(kol_mang));

                URL2 = el2.attr("href");
                if (!URL2.contains("mangachan"))
                    URL2 = classMang.getURL() + URL2;

                nameChar = el2.text();
               // Log.i("NameURL",classMang.getNameURL());
               // Log.i("el2.attr()",classMang.getURL() + el2.attr("href"));

                el2 = el.select(classMang.getImgURL());
                imgSrc = el2.attr("src");
                if (!imgSrc.contains("mangachan")){
                    imgSrc = "http://mangachan.ru" + imgSrc;
                }

                if (list.size() >= 10){
                    if (nextPage){
                        endDownload = reiteration(nameChar);
                    }
                }
              //  Log.i("imgSrc",imgSrc);
               // Log.i("nameChar",nameChar);
                if (kol_mang == 9) doc = null;
            } catch (IOException e) {
                //System.out.println("Не грузит страницу либо больше нечего грузить");
                e.printStackTrace();
                not_net = true;
                try {
                    if (!e.getMessage().isEmpty())
                        errorMassage += " " + e.getMessage();
                }catch (NullPointerException ei){
                    ei.printStackTrace();
                }
                stopLoad = true;
            } catch (Exception e){
                Crashlytics.setString("mangUrl",classMang.getURL() + classMang.getWhereAll());
                Crashlytics.logException(e);
                stopLoad = true;
            }
            return null;
        }

        private boolean reiteration(String name){
            return list.get(list.size()-10).getNameCharacher().contains(name);
        }

        private String getWhere(int amt){
            if (classMang.getPath2().isEmpty()) {
                return classMang.getWhere() + amt + classMang.getPath();
            }else {
                return classMang.getWhere() + classMang.getPath() + amt + classMang.getPath2();
            }
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(Void result){
            //добавляем в лист и обновление/*
          /*  if (endDownload){
                stopLoad = true;
                return;
            }*/
            if (!not_net){
                if (kol >= 0 && !URL2.isEmpty() && !nameChar.isEmpty() && !classMang.getURL().isEmpty() && !endDownload) {
                   /* String locNameChar = nameChar;
                    locNameChar = locNameChar.replace(")","").replace(" ","").replace("(",",");*/
                    ClassMainTop classMainTop = new ClassMainTop(URL2, nameChar,imgSrc,classMang.getURL());
                    if (DEBUG)
                        Log.i("Temple Pase: Kol parse: ", String.valueOf(kol));
                    try {
                        if (list.size() <= kol)
                            list.add(classMainTop);
                        if (resultPost == 0) {
                            classDataBaseListMang.addBasaData(classMainTop, kol);
                        }
                        myAdap.notifyDataSetChanged();
                    }catch (IndexOutOfBoundsException e){
                        //
                        Log.i("Temple Pase: Error: ",e.toString());
                        Log.i("Temple Pase: Size list: ", String.valueOf(list.size()));
                        Log.i("Temple Pase: Kol: ", String.valueOf(kol));
                    }
                    //кричим интерфейсу что мы фсе
                    if (lisens != null) lisens.onEnd();
                }
            }else {
                try{
                    Toast.makeText(context, errorMassage, Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e){

                }
            }
            if (lisens != null) lisens.onEnd(1);
        }
    }
}
