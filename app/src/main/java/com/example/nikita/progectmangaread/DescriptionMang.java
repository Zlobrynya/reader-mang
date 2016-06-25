package com.example.nikita.progectmangaread;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterSlidingMenu;
import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseViewedHead;
import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseListMang;
import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classDescriptionMang;
import com.example.nikita.progectmangaread.classPMR.classForList;
import com.example.nikita.progectmangaread.classPMR.classTransportForList;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionList;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionMang;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 01.02.2016.
 * Описание манги, нужно стьделать пролистывание фрагментов
 * 1 фрагмент описание, 2й фрагмент лист глав
 * Разделить парсер страницы на 2:
 *  1. Парсим описание
 *  2. Парсим список глав и делаем инверсию.
 *
 */

public class DescriptionMang extends BaseActivity {
    public Document doc;
    private MainClassTop mang;
    public ArrayList<classForList> arList;
    public int kol;
    private ViewPager pager;
    private adapterFragment gg;
    private Pars pars;
    private Element el;
    private boolean read;
    private FloatingActionButton fab1,fab2,fab3;
    private Animation show_fab_1;
    private Animation hide_fab_1;
    private boolean visF = false;
    private boolean bookmark = false;
    private classDataBaseViewedHead classDataBaseViewedHead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kol = 0;
        getLayoutInflater().inflate(R.layout.layout_description_mang, frameLayout);
        pager=(ViewPager)findViewById(R.id.pager);
        gg = new adapterFragment(getSupportFragmentManager(),2);
        pager.setAdapter(gg);
        arList = new ArrayList<classForList>();
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        read = intent.getBooleanExtra("read", false);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_lastChapter);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_notebook);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_download);
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visF){
                    buttonINVisble();
                    visF = !visF;
                }else {
                    buttonVisible();
                    visF = !visF;

                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLastChapter();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookmark){
                    fab2.setImageResource(R.drawable.ic_favorite_white_48dp);
                    Toast.makeText(view.getContext(), "Add bookmark", Toast.LENGTH_SHORT).show();
                    bookmark = false;
                }else {
                    fab2.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                    Toast.makeText(view.getContext(), "Delete bookmark", Toast.LENGTH_SHORT).show();
                    bookmark = true;
                }
                EventBus.getDefault().post("notebook");
            }
        });


    }

    //Метод для открытия бокового меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {  // узнаем ID нажатой кнопки
            case android.R.id.home: // если это кнопка-иконка ActionBar,
            //    menu.toggle(true);        // открываем меню (или закрываем)
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            ParsList parsList = new ParsList();
            parsList.execute();
        }

        @Override
        public void onEnd(InputStream is) {

        }
    };

    private void buttonVisible(){
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        fab2.startAnimation(show_fab_1);
        fab2.setClickable(true);

        fab3.startAnimation(show_fab_1);
        fab3.setClickable(true);
    }

    private void buttonINVisble(){

        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        fab2.startAnimation(hide_fab_1);
        fab2.setClickable(false);

        fab3.startAnimation(hide_fab_1);
        fab3.setClickable(false);

    }

    //ПОЛУЧАЕМ с топлиста
    public void onEvent(MainClassTop event){
        mang = event;
        pars = new Pars(addImg,mang);
        pars.execute();
        //ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle(event.getName_characher()); // set the top title
        classDataBaseViewedHead = new classDataBaseViewedHead(this,mang.getName_characher());
        if (classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(),classDataBaseViewedHead.NOTEBOOK).contains("1")){
            fab2.setImageResource(R.drawable.ic_favorite_white_48dp);
            bookmark = false;
        }else {
            bookmark = true;
            fab2.setImageResource(R.drawable.ic_favorite_border_white_48dp);
        }
        if (read) startLastChapter();
    }

    public void onEvent(classForList URL){
        //узнаем нужно ли запускать активити
        if (URL.getNumberChapter() >= 0){
            Intent intent = new Intent(this, pagesDownload.class);
            intent.putExtra("URL", mang.getURL_site()+URL.getURL_chapter());
            intent.putExtra("NumberChapter", URL.getNumberChapter());

            classDataBaseViewedHead.editLastChapter(mang.getName_characher(), mang.getURL_site() + URL.getURL_chapter());
            intent.putExtra("NumberPage", classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), classDataBaseViewedHead.LAST_PAGE));
            intent.putExtra("Chapter", mang.getName_characher());
            startActivity(intent);
        }
    }
    //Процедура для кнопки
    public void StartRead(View view) {
        startLastChapter();
    }

    private void startLastChapter(){
        String string = classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), classDataBaseViewedHead.LAST_CHAPTER);
        int numberChapter = numberLastChapter();
        int numberPage = 0;
        if (!read){
            //numberChapter = arList.size()-1;
            if (string.contains("null")){
                classForList classForList = arList.get(numberChapter);
                string = mang.getURL_site()+classForList.getURL_chapter();
            }
        }

        if (!string.contains(mang.getURL_site())){
            string = mang.getURL_site() + string;
        }
        //  classForList classForList = arList.get(numberChapter);
        Intent intent = new Intent(this, pagesDownload.class);
        intent.putExtra("URL",string);
        intent.putExtra("NumberChapter", numberChapter);
        intent.putExtra("NumberPage",classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), classDataBaseViewedHead.LAST_PAGE));
        intent.putExtra("Chapter", mang.getName_characher());
        startActivity(intent);
    }

    private int numberLastChapter(){
        String string = classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), classDataBaseViewedHead.LAST_CHAPTER);
       /* string = string.replace(" - ", " ");
        string = string.replace("-", "");
        string = string.replace(",", "");

        //Узнаем сколько слов в строке
        int world = string.split(" ").length;
        //Удаляем по одному слову пока не останится одно слово
        for (int i = 0; i < world-2;i++){
            string = string.replaceFirst("\\w+\\s+","");
        }*/

        Log.i("Number chapter", string);
        Short kol = 0;
        for (classForList c : arList){
            String name = c.getURL_chapter();
            Log.i("Number chapter",name);
            if (string.contains(name)) {
                Log.i("Number chapter", String.valueOf(kol));
                //Отправляем в pagesDowload, если было запущено с закладок
                if (read) EventBus.getDefault().post(kol);
                return kol;
            }
            kol++;
        }
        return 0;
    }

    public class adapterFragment  extends FragmentPagerAdapter {
        int kol;
        public adapterFragment(FragmentManager mgr, int kol) {
            super(mgr);
            this.kol = kol;
        }
        @Override
        public int getCount() {
            return(kol);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Описание";
                case 1:
                    return "Главы";
                default:
                    return "Magic";
            }
        }

        @Override
        public Fragment getItem(int position) {
           if (position == 0) return fragmentDescriptionMang.newInstance(position);
            else return fragmentDescriptionList.newInstance(position);
        }
    }

    public class Pars extends AsyncTask<Void,Void,Void> {
        private MainClassTop mang;
        private AsyncTaskLisen lisens;
        private com.example.nikita.progectmangaread.classPMR.classDescriptionMang classDescriptionMang;

        //конструктор потока
        protected Pars(AsyncTaskLisen callback,MainClassTop mang) {
            this.mang = mang;
            this.lisens = callback;
            classDescriptionMang = new classDescriptionMang();
            classDescriptionMang.setNameMang(mang.getName_characher());
            classDescriptionMang.setImg_url(mang.getURL_img());
        }

        @Override
        protected  void  onPreExecute(){ super.onPreExecute(); }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                if (doc == null) doc = Jsoup.connect(mang.getURL_characher()).get();

                Element el = doc.select("[class = small smallText rate_info]").first();
                classDescriptionMang.setRank("Рейтинг:" + el.select("b").first().text());

                //считываем тома
                el = doc.select("[class = subject-meta col-sm-7]").first();
                el = el.select("p").first();
                classDescriptionMang.setToms(el.select("p").first().text());
                el = el.nextElementSibling();
                el = el.nextElementSibling();
                //считываем
                classDescriptionMang.setTranslate(el.select("p").text());
                for (int i = 0; i < 4;i++){
                    el = el.nextElementSibling();
                    String helpVar = el.text();
                    Elements el4;
                    if (helpVar.contains("Жанры")){
                        classDescriptionMang.setGenre(helpVar);
                    }else if (helpVar.contains("Автор")){
                        classDescriptionMang.setNameAuthor(el.text());
                    }else {
                        el4 = el.select("b");
                        String category;
                        if (mang.getURL_characher().contains("readmanga.me")){
                            category = "Категории";
                        }else category = "Категория";
                        if (el4.text().contains(category)){
                            classDescriptionMang.setCategory(el.text());
                        }
                    }
                }
                //описание выбора http://jsoup.org/apidocs/org/jsoup/select/Selector.html
                Elements el2 = doc.select("[itemprop = description]");
                classDescriptionMang.setDescription(el2.attr("content"));
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                System.out.println("Не грузит страницу либо больше нечего грузить");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            EventBus.getDefault().post(classDescriptionMang);
            if (lisens != null) lisens.onEnd();
        }
    }

    public class ParsList extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            el = doc.select("[class = table table-hover]").first();
            if (el != null){
                el = el.select("tbody").first();
                el = el.select("tr").first();
                do {
                    parsList();
                }while (el != null);
            }

            return null;
        }

        void parsList(){
            classForList classForList = new classForList();
            classForList.setNumberChapter(-1);
            Elements el2 = el.select("a");
            String URL = el2.attr("href");
            classForList.setURL_chapter(URL);
            URL = el2.select("a").text();
            classForList.setName_chapter(URL);
            if (!classForList.getName_chapter().isEmpty()) arList.add(classForList);
            el = el.nextElementSibling();
        }

        @Override
        protected void onPostExecute(Void result){
            classTransportForList classTransportForList = new classTransportForList(arList,mang.getName_characher(),mang);
            EventBus.getDefault().post(classTransportForList);
            if (read){
                numberLastChapter();
                read = false;
            }
        }
    }
}
