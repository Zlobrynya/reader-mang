package com.zlobrynya.project.readermang.ParsSite.ReadManga;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.AdapterPMR.AdapterMainScreen;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseListMang;
import com.zlobrynya.project.readermang.ParsSite.ParsTopList;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassMang;
import com.zlobrynya.project.readermang.fragment.fragmentTopMang;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by Nikita on 03.01.2017.
 */

public class ParsTopListRM extends ParsTopList {


    public ParsTopListRM(Context context, LinkedList<ClassMainTop> list){
        super(context,list);
    }

    //метод парсим
    public void parssate(int kol){
        //парсим сайт
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
                // Проверка на то что сейчас парсим, если запрос то там без страниц идет
                if (resultPost != 1){
                    page = kol / classMang.getMaxInPage();
                    kol_mang = kol - (classMang.getMaxInPage()*page);
                }else kol_mang = kol;


                if (doc == null){
                    classMang.editWhere(page);
                    Connection.Response response = Jsoup.connect(classMang.getURL() + classMang.getWhereAll())
                            ///5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.2
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                            .timeout(100000)
                            .ignoreHttpErrors(true)
                            .execute();
                    int statusCode = response.statusCode();
                    if (statusCode == 200){
                        doc = Jsoup.connect(classMang.getURL() + classMang.getWhereAll())
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

                URL2 = classMang.getURL() + el2.attr("href");
                el2 = el.select(classMang.getImgURL());

                imgSrc = el2.attr("src");
                nameChar = el2.attr("title");
                if (kol_mang == 69 && resultPost != 1) doc = null;
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

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(Void result){
            //добавляем в лист и обновление
            if (!not_net){
                if (kol >= 0 && !URL2.isEmpty() && !nameChar.isEmpty() && !classMang.getURL().isEmpty()) {
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
