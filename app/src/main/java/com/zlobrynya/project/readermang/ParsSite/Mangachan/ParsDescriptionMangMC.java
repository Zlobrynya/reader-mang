package com.zlobrynya.project.readermang.ParsSite.Mangachan;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.Activity.BaseActivity;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.ParsSite.ParsDescriptionMang;
import com.zlobrynya.project.readermang.ParsSite.ReadManga.ParsDescriptionMangRM;
import com.zlobrynya.project.readermang.classPMR.ClassDescriptionMang;
import com.zlobrynya.project.readermang.classPMR.ClassForList;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassOtherMang;
import com.zlobrynya.project.readermang.classPMR.ClassTransportForList;

import org.greenrobot.eventbus.EventBus;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nikita on 19.02.2017.
 */

public class ParsDescriptionMangMC extends ParsDescriptionMang {

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

    public ParsDescriptionMangMC(BaseActivity baseActivity, ArrayList<ClassForList> arList, FloatingActionButton fab, boolean otherMang, ClassMainTop mang) {
        super(baseActivity, arList, fab, otherMang, mang);
    }

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

                Log.i("URL",mang.getURLCharacher());
                Element el;

                //если пришли со вкладки "другие манги" нужно достать имя манги на русском для бд
                if (otherMang){
                    el = doc.select("[class = title_top_a]").first();
                    name = el.text();
                }
                //считываем тома
                el = doc.select("[id = info_wrap]").first();
                //Получаем количетво томов
                el = el.select("tr").first();

                for (int i = 0; i < 5;i++){
                    el = el.nextElementSibling();
                    if (el == null) break;
                    String helpVar = el.select("[class = item]").text();
                    if (helpVar.contains("Тэги")){
                        helpVar = el.select("[class = item2]").text();
                        helpVar = helpVar.replace("_"," ");
                        ClassDescriptionMang.setGenre(helpVar);
                    }else if (helpVar.contains("Автор")){
                        ClassDescriptionMang.setNameAuthor(helpVar + ": " + el.select("[class = item2]").text());
                    }else if (helpVar.contains("Тип")){
                        ClassDescriptionMang.setCategory(helpVar + ": " + el.select("[class = item2]").text());
                    }else if (helpVar.contains("Загружено")){ //Перевод
                        ClassDescriptionMang.setTranslate(helpVar + ": " + el.select("[class = item2]").text());
                    }else if (helpVar.contains("Статус")) { //Тома
                        ClassDescriptionMang.setToms(helpVar + ": " + el.select("[class = item2]").text());
                    }
                }
                if (ClassDescriptionMang.getTranslate().isEmpty()){
                    ClassDescriptionMang.setTranslate("Перевод: завершен");
                }
                //описание выбора http://jsoup.org/apidocs/org/jsoup/select/Selector.html
                Elements el2 = doc.select("[id=description]");
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
        private String urlSimilar;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                el = doc.select("[class = table_cha]").first();
                if (el != null){
                    do {
                        parsList();
                    }while (el != null);
                }
                el = doc.select("[class = extra_off]").first();
                urlSimilar = el.attr("href");
            }catch (NullPointerException e){
                // Log.getStackTraceString(e);
            }
            return null;
        }

        void parsList(){
            Element el2 = el.select("tr").first();
            el2 = el2.nextElementSibling();
            el2 = el2.nextElementSibling();
            Log.i("URl","START");
            if (el2 != null){
                do {
                    Element el3 = el2.select("[class = manga] a").first();
                    ClassForList classForList = new ClassForList();
                    classForList.setNumberChapter(-1);
                    String URL = el3.attr("href");
                    Log.i("URl",URL);
                    classForList.setURL_chapter(URL);
                    URL = el2.select("a").text();
                    Log.i("URl",URL);
                    Log.i("URl","---");
                    classForList.setNameChapter(URL);
                    if (!classForList.getNameChapter().isEmpty())
                        arList.add(classForList);
                    el2 = el2.nextElementSibling();
                }while (el2 != null);
                el = el.nextElementSibling();
            }
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
                parsList.execute(urlSimilar);
            }
        }
    }

    private class ParsSimilarAndRelatedMang extends AsyncTask<String,Void,Void>{
        private Element el;

        @Override
        protected Void doInBackground(String... params) {
            if (classOtherManglist == null)
                classOtherManglist = new ArrayList<>();
            try {
                doc = Jsoup.connect(mang.getUrlSite()+"/related"+mang.getURLCharacher().substring(mang.getURLCharacher().lastIndexOf("/"))).userAgent("Mozilla")
                        .timeout(3000)
                        .get();
                //Crashlytics.log("Description Mang, отсутствуе похожие манги");
                //перемещаемся на след.таблицу с похожей мангой
                parsRelated();
            } catch (IOException | NullPointerException e) {
                //e.printStackTrace();+

                //Log.i("Log","work");
            }

            return null;
        }

        void parsRelated(){
            //tiles row
            el = doc.select("[class = related]").first();
            try{
                do {
                    ClassOtherMang classOtherMang = new ClassOtherMang();
                    Elements elements = el.select("[class = related_info] a");
                    if (elements != null){
                        classOtherMang.setURLchapter(mang.getUrlSite() + elements.attr("href"));
                        classOtherMang.setNameMang(elements.attr("title"));
                        if (classOtherMang.getNameMang().isEmpty())
                            break;
                        //
                        elements = el.select("[class = related_cover] img");
                    /* if (element.select("sup") != null){
                               element = element.nextElementSibling();
                           }*/
                        String urlImg = elements.attr("src");
                        if (!urlImg.contains("mangachan")){
                            urlImg = mang.getUrlSite() + urlImg;
                        }
                        classOtherMang.setURL_img(urlImg);
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
