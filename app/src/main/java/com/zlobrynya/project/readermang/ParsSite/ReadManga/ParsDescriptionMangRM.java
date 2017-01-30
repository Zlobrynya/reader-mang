package com.zlobrynya.project.readermang.ParsSite.ReadManga;


import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.Activity.BaseActivity;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.ParsSite.ParsDescriptionMang;
import com.zlobrynya.project.readermang.classPMR.ClassDescriptionMang;
import com.zlobrynya.project.readermang.classPMR.ClassForList;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassOtherMang;
import com.zlobrynya.project.readermang.classPMR.ClassTransportForList;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nikita on 11.01.2017.
 */

public class ParsDescriptionMangRM extends ParsDescriptionMang {

    public ParsDescriptionMangRM(BaseActivity baseActivity, ArrayList<ClassForList> arList, FloatingActionButton fab, boolean otherMang){
        super(baseActivity,arList,fab,otherMang);
    }

    private AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            ParsList parsList = new ParsList();
            parsList.execute();
        }

        @Override
        public void onEnd(int number) {

        }
    };

    public void startPars(){
        Pars pars = new Pars(addImg, mang);
        pars.execute();
    }

    private class Pars extends AsyncTask<Void,Void,Void> {
        private ClassMainTop mang;
        private AsyncTaskLisen lisens;
        private com.zlobrynya.project.readermang.classPMR.ClassDescriptionMang ClassDescriptionMang;
        private boolean not_net; //отвечает за проверку подклчение
        private String name,errorMassage;

        //конструктор потока
        Pars(AsyncTaskLisen callback, ClassMainTop mang) {
            this.mang = mang;
            this.lisens = callback;
            ClassDescriptionMang = new ClassDescriptionMang();
            ClassDescriptionMang.setNameMang(mang.getNameCharacher());
            ClassDescriptionMang.setImg_url(mang.getUrlImg());
            not_net = false;
            errorMassage = "Ошибка подключения.";
        }

        @Override
        protected  void  onPreExecute(){ super.onPreExecute(); }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                if (doc == null){
                    Connection.Response response = Jsoup.connect(mang.getURLCharacher())
                            ///5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.2
                            .userAgent("Mozilla")
                            .timeout(100000)
                            .ignoreHttpErrors(true)
                            .execute();
                    int statusCode = response.statusCode();
                    if (statusCode == 200){
                        doc = Jsoup.connect(mang.getURLCharacher())
                                .userAgent("Mozilla")
                                .timeout(100000)
                                .get();
                    }else {
                        errorMassage = response.statusMessage() + " : " + response.statusCode();
                        not_net = true;
                        return null;
                    }
                }

                //если пришли со вкладки "другие манги" нужно достать имя манги на русском для бд
                Element el = doc.select("[class = small smallText rate_info]").first();
                ClassDescriptionMang.setRank("Рейтинг:" + el.select("b").first().text());
                if (otherMang){
                    el = doc.select("[class = name]").first();
                    name = el.text();
                }
                //считываем тома
                el = doc.select("[class = subject-meta col-sm-7]").first();
                //Получаем количетво томов
                el = el.select("p").first();
                ClassDescriptionMang.setToms(el.select("p").first().text());

                for (int i = 0; i < 7;i++){
                    el = el.nextElementSibling();
                    if (el == null) break;
                    String helpVar = el.text();
                    if (helpVar.contains("Жанры")){
                        helpVar = "";
                        Elements elements = el.select("[class ^= elem_]");
                        for (Element element: elements){
                            helpVar += element.text();
                        }
                        ClassDescriptionMang.setGenre(helpVar);
                    }else if (helpVar.contains("Автор")){
                        ClassDescriptionMang.setNameAuthor(el.text());
                    }else if (helpVar.contains("Категор")){
                        ClassDescriptionMang.setCategory(el.text());
                    }else if (helpVar.contains("Перевод:")){
                        ClassDescriptionMang.setTranslate(el.text());
                    }else if (helpVar.contains("Год")){
                        break;
                    }
                }
                if (ClassDescriptionMang.getTranslate().isEmpty()){
                    ClassDescriptionMang.setTranslate("Перевод: завершен");
                }
                //описание выбора http://jsoup.org/apidocs/org/jsoup/select/Selector.html
                Elements el2 = doc.select("[class = manga-description]");
                ClassDescriptionMang.setDescription(el2.text());
            } catch (IOException e) {
                e.printStackTrace();
                try{
                    if (!e.getMessage().isEmpty())
                        errorMassage += " " + e.getMessage();
                }catch (NullPointerException e1){

                }

                not_net = true;
            }catch (Exception e) {
                //  e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if (!not_net){
                if (otherMang){
                    mang.setNameCharacher(name+" ("+mang.getNameCharacher()+")");
                    baseActivity.getSupportActionBar().setTitle(mang.getNameCharacher()); // set the top title
                }
                classDescriptionMang = ClassDescriptionMang;
                EventBus.getDefault().post(ClassDescriptionMang);
                if (lisens != null) lisens.onEnd();
                fab.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(baseActivity, errorMassage, Toast.LENGTH_SHORT).show();
                baseActivity.finish();
            }
        }
    }

    private class ParsList extends AsyncTask<Void,Void,Void> {
        private Element el;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                el = doc.select("[class = table table-hover]").first();
                if (el != null){
                    el = el.select("tbody").first();
                    el = el.select("tr").first();
                    do {
                        parsList();
                    }while (el != null);
                }
            }catch (NullPointerException e){
                // Log.getStackTraceString(e);
            }
            return null;
        }

        void parsList(){
            ClassForList classForList = new ClassForList();
            classForList.setNumberChapter(-1);
            Elements el2 = el.select("a");
            String URL = el2.attr("href");
            classForList.setURL_chapter(URL);
            URL = el2.select("a").text();
            classForList.setNameChapter(URL);
            if (!classForList.getNameChapter().isEmpty()) arList.add(classForList);
            el = el.nextElementSibling();
        }

        @Override
        protected void onPostExecute(Void result){
            if (!arList.isEmpty()){
                ClassTransportForList transportForList = new ClassTransportForList(arList,mang.getNameCharacher(),mang);
                classTransportForList = transportForList;
                // classDataBaseViewedHead.setData(mang.getNameCharacher(), String.valueOf(arList.size()),ClassDataBaseViewedHead.QUANTITY);
                EventBus.getDefault().postSticky(transportForList);
                // Без понятия, что это
                /* if (read){
                    numberLastChapter();
                    read = false;
                }*/
                ParsSimilarAndRelatedMang parsList = new ParsSimilarAndRelatedMang();
                parsList.execute();
            }
        }
    }

    private class ParsSimilarAndRelatedMang extends AsyncTask<Void,Void,Void>{
        private Element el;

        @Override
        protected Void doInBackground(Void... params) {
            if (classOtherManglist == null)
                classOtherManglist = new ArrayList<>();
            try {
                doc = Jsoup.connect(mang.getUrlSite()+"/list/like"+mang.getURLCharacher().substring(mang.getURLCharacher().lastIndexOf("/"))).userAgent("Mozilla")
                        .timeout(3000)
                        .get();
                //вытаскиваем первую таблицу со связаной мангой
                try{
                    parsSimilar(0);
                }catch(NullPointerException e){
                    Crashlytics.logException(e);
                    Crashlytics.setString("mangUrl",mang.getUrlSite()+"/list/like"+mang.getURLCharacher().substring(mang.getURLCharacher().lastIndexOf("/")));
                    Crashlytics.setString("What","Связаные");
                }
                try{
                    parsSimilar(1);
                }catch(NullPointerException e){
                    Crashlytics.logException(e);
                    Crashlytics.setString("mangUrl",mang.getUrlSite()+"/list/like"+mang.getURLCharacher().substring(mang.getURLCharacher().lastIndexOf("/")));
                    Crashlytics.setString("What","Похожие");
                }
                //Crashlytics.log("Description Mang, отсутствуе похожие манги");
                //перемещаемся на след.таблицу с похожей мангой
                parsRelated();
            } catch (IOException | NullPointerException e) {
                //e.printStackTrace();+

                //Log.i("Log","work");
            }

            return null;
        }

        void parsSimilar(int number){
            String category = "similar";
            Element element = doc.select("h2").first();
            if (number == 0 && element.text().contains("Похожее"))
                return;
            el = doc.select("[class = table table-hover]").select("tr").first();
            if (number > 0){
                el = doc.select("[class = table table-hover]").last();
                el = el.select("tr").first();
                category = "related";
            }

            if (el != null){
                do {
                    if (!el.text().contains("Аниме")){
                        ClassOtherMang classOtherMang = new ClassOtherMang();
                        Elements elements = el.select("td");
                        element = elements.select("[class = manga-link]").first();
                        if (element != null){
                            classOtherMang.setURLchapter(mang.getUrlSite() + element.attr("href"));
                            classOtherMang.setNameMang(element.text());
                            element = elements.select("[class = screenshot]").first();
                           /* if (element.select("sup") != null){
                                element = element.nextElementSibling();
                            }*/
                            classOtherMang.setURL_img(element.attr("rel"));
                            classOtherMang.setNameCategory(category); //тег что это связаное произведение
                            classOtherMang.setUrlSite(mang.getUrlSite());
                            classOtherManglist.add(classOtherMang);
                        }
                        el = el.nextElementSibling();
                    }else break;
                }while (el != null);
            }
        }

        void parsRelated(){
            //tiles row
            el = doc.select("[class = tiles row]").select("[class = tile col-sm-6]").first();
            try{
                do {
                    ClassOtherMang classOtherMang = new ClassOtherMang();
                    Elements elements = el.select("a");
                    if (elements != null){
                        classOtherMang.setURLchapter(mang.getUrlSite() + elements.attr("href"));
                        //
                        elements = el.select("img");
                    /* if (element.select("sup") != null){
                               element = element.nextElementSibling();
                           }*/
                        classOtherMang.setNameMang(elements.attr("title"));
                        classOtherMang.setURL_img(elements.attr("src"));
                        classOtherMang.setNameCategory("related"); //тег что это связаное произведение
                        classOtherMang.setUrlSite(mang.getUrlSite());
                        classOtherManglist.add(classOtherMang);
                    }
                    el = el.nextElementSibling();
                }while (el != null);
            }catch (NullPointerException ignored){

            }
        }

        @Override
        protected void onPostExecute(Void result){
            EventBus.getDefault().postSticky(classOtherManglist);
        }
    }
}
