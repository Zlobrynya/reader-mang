package com.example.nikita.progectmangaread.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseDownloadMang;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassMainTop;
import com.example.nikita.progectmangaread.classPMR.ClassDescriptionMang;
import com.example.nikita.progectmangaread.classPMR.ClassForList;
import com.example.nikita.progectmangaread.classPMR.ClassTransportForList;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionList;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionMang;
import com.example.nikita.progectmangaread.fragment.fragmentSaveDescriptionMang;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
    private Document doc;
    private ClassMainTop mang;
    private ArrayList<ClassForList> arList;
    private ViewPager pager;
    private adapterFragment gg;
    private Pars pars;
    private Element el;
    private boolean read, downloadChapter;
    private FloatingActionButton fab1,fab2,fab3,fab;
    private Animation show_fab_1;
    private Animation hide_fab_1;
    private boolean visF = false;
    private boolean bookmark = false;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private fragmentSaveDescriptionMang saveFragment;
    private ClassDescriptionMang descriptionMang;
    private ClassTransportForList classTransportForList;

    private final String PROBLEM = "ProblemTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.description_mang, frameLayout);

     //   Log.i(PROBLEM, "Description start");
        pager=(ViewPager)findViewById(R.id.pager);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_lastChapter);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_notebook);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_download);
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        gg = new adapterFragment(getSupportFragmentManager(),2);
        pager.setAdapter(gg);
        arList = new ArrayList<>();
        read = false;

        final Intent intent = getIntent();
        if (intent.getStringExtra("URL_ch") != null){
            mang = new ClassMainTop();
            mang.setURL_characher(intent.getStringExtra("URL_ch"));
            mang.setName_characher(intent.getStringExtra("Name_ch"));
            mang.setURL_site(intent.getStringExtra("Url_site"));
            mang.setURL_img(intent.getStringExtra("Url_img"));
            read = intent.getBooleanExtra("read", false);
            parsAndSettings();
        }
        downloadChapter = false;
        dataRecovery();

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (visF) {
                    buttonINVisble();
                    visF = !visF;
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visF) {
                    buttonINVisble();
                    visF = !visF;
                } else {
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

        //Кнопка скачивания
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClassDataBaseDownloadMang classDataBaseDownloadMang = new ClassDataBaseDownloadMang(DescriptionMang.this);
                classDataBaseDownloadMang.addBasaData(mang.getName_characher());
                //добавляем в бд описание манги и т.п.
                classDataBaseDownloadMang.setData(mang.getName_characher(), mang.getURL_characher(), ClassDataBaseDownloadMang.URL_MANG);
                classDataBaseDownloadMang.setData(mang.getName_characher(), descriptionMang.getRank(), ClassDataBaseDownloadMang.RATING);
                classDataBaseDownloadMang.setData(mang.getName_characher(), descriptionMang.getCategory(), ClassDataBaseDownloadMang.CATEGORY);
                classDataBaseDownloadMang.setData(mang.getName_characher(), descriptionMang.getDescription(), ClassDataBaseDownloadMang.DESCRIPTION);
                classDataBaseDownloadMang.setData(mang.getName_characher(), descriptionMang.getGenre(), ClassDataBaseDownloadMang.GENRES);
                classDataBaseDownloadMang.setData(mang.getName_characher(), descriptionMang.getNameAuthor(), ClassDataBaseDownloadMang.AUTHOR);
                classDataBaseDownloadMang.setData(mang.getName_characher(), descriptionMang.getToms(), ClassDataBaseDownloadMang.TOMS);
                classDataBaseDownloadMang.setData(mang.getName_characher(), descriptionMang.getTranslate(), ClassDataBaseDownloadMang.TRANSLATION);
                classDataBaseDownloadMang.closeDataBase();

                Intent newInten = new Intent(DescriptionMang.this, DownloadChapter.class);
                newInten.putExtra("mang", mang.getURL_characher());
                newInten.putExtra("site", mang.getURL_site());
                newInten.putExtra("Name", mang.getName_characher());
                newInten.putExtra("image",mang.getURL_img());
                startActivity(newInten);
                downloadChapter = true;
            }
        });

       // Log.i(PROBLEM, "End onCreate");
        EventBus.getDefault().register(this);

    }

    private void dataRecovery(){
        saveFragment = (fragmentSaveDescriptionMang) getFragmentManager().findFragmentByTag("SAVE_FRAGMENT");

        if (saveFragment != null){
            classTransportForList = saveFragment.getClassTransportForList();
            descriptionMang = saveFragment.getClassDescriptionMang();
            mang = saveFragment.getMang();
            classDataBaseViewedHead = new ClassDataBaseViewedHead(this,mang.getName_characher());
            classDataBaseViewedHead = new ClassDataBaseViewedHead(this,mang.getName_characher());
            if (classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(),ClassDataBaseViewedHead.NOTEBOOK).contains("1")){
                fab2.setImageResource(R.drawable.ic_favorite_white_48dp);
                bookmark = false;
            }else {
                bookmark = true;
                fab2.setImageResource(R.drawable.ic_favorite_border_white_48dp);
            }
        }
        else {
            saveFragment = new fragmentSaveDescriptionMang();
            getFragmentManager().beginTransaction()
                    .add(saveFragment, "SAVE_FRAGMENT")
                    .commit();
        }
    }

    @Override
    public void onDestroy(){
        if (classDataBaseViewedHead != null)
            classDataBaseViewedHead.closeDataBase();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
       //
        if (downloadChapter)
            EventBus.getDefault().post(classTransportForList);
        super.onStop();
    }

    @Override
    public void onResume() {
        //Зачем это нужно?
      /*  if (descriptionMang != null && classTransportForList != null){
            //EventBus.getDefault().post(descriptionMang);
            //EventBus.getDefault().post(classTransportForList);
        }*/
        super.onResume();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveFragment.setClassDescriptionMang(descriptionMang);
        saveFragment.setClassTransportForList(classTransportForList);
        saveFragment.setMang(mang);
        super.onSaveInstanceState(outState);
    }
    // Получаем событие, что был клик на экран и это не фабкнопка
    // И если кнопки показаны, то их закрываем
    public void onEvent(String event){
        if (event.contains("Click")){
            if (visF){
                buttonINVisble();
                visF = !visF;
            }
        }
    }

    //ПОЛУЧАЕМ с топлиста
    public void parsAndSettings(){
        Log.i(PROBLEM, "Get ClassMainTop event");

       // mang = event;
        pars = new Pars(addImg,mang);
        pars.execute();
        //ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle(mang.getName_characher()); // set the top title
        classDataBaseViewedHead = new ClassDataBaseViewedHead(this,mang.getName_characher());
        if (classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(),classDataBaseViewedHead.NOTEBOOK).contains("1")){
            fab2.setImageResource(R.drawable.ic_favorite_white_48dp);
            bookmark = false;
        }else {
            bookmark = true;
            fab2.setImageResource(R.drawable.ic_favorite_border_white_48dp);
        }
        if (read) startLastChapter();
    }

    public void onEvent(ClassForList event){
        //узнаем нужно ли запускать активити
        if (event.getNumberChapter() >= 0){
            Intent intent = new Intent(this, PagesDownload.class);
            if (!event.getDownload()){
                intent.putExtra("URL", mang.getURL_site() + event.getURL_chapter());
                intent.putExtra("NumberChapter", event.getNumberChapter());
                classDataBaseViewedHead.editLastChapter(mang.getName_characher(), mang.getURL_site() + event.getURL_chapter());
                String helpVar = classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), classDataBaseViewedHead.LAST_PAGE);
                if (helpVar.contains("null")) helpVar = "1";
                intent.putExtra("NumberPage",helpVar);
                intent.putExtra("Chapter", mang.getName_characher());
                startActivity(intent);
            }else {
                intent.putExtra("URL", event.getURL_chapter());
                intent.putExtra("NumberChapter", event.getNumberChapter());
                intent.putExtra("Download",true);
                intent.putExtra("NumberPage","1");
                intent.putExtra("Chapter", event.getName_chapter());
                startActivity(intent);
            }

        }
    }

    //Процедура для кнопки
    public void StartRead(View view) {
        startLastChapter();
    }

    private void startLastChapter(){
        String string = classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), classDataBaseViewedHead.LAST_CHAPTER);
        try {
            int numberChapter = numberLastChapter();
            if (!read){
                //numberChapter = arList.size()-1;
                if (string.contains("null")){
                    ClassForList classForList = arList.get(numberChapter);
                    string = mang.getURL_site()+classForList.getURL_chapter();
                    classDataBaseViewedHead.setData(mang.getName_characher(), String.valueOf(numberChapter),classDataBaseViewedHead.VIEWED_HEAD);
                }
            }
            //Получаем дату и пишем в БД
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c.getTime());
            classDataBaseViewedHead.setData(mang.getName_characher(), formattedDate,classDataBaseViewedHead.DATA);

            if (!string.contains(mang.getURL_site())){
                string = mang.getURL_site() + string;
            }

            Intent intent = new Intent(this, PagesDownload.class);
            intent.putExtra("URL",string);
            intent.putExtra("NumberChapter", numberChapter);
            intent.putExtra("NumberPage",classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), classDataBaseViewedHead.LAST_PAGE));
            intent.putExtra("Chapter", mang.getName_characher());
            startActivity(intent);
        }catch (java.lang.ArrayIndexOutOfBoundsException e){
            Toast toast = Toast.makeText(this,
                    "В манге нет глав", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private int numberLastChapter() throws ArrayIndexOutOfBoundsException{
        String string = classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), classDataBaseViewedHead.LAST_CHAPTER);
       // Log.i("Number chapter", string);
        Short kol = 0;
        //Проверка на первый раз, если так то выдаем самую последнюю главу в списке
        if (string.contains("null")) return arList.size()-1;

        for (ClassForList c : arList){
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

    //Открываем сылку в браузере
    public void openURL(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mang.getURL_characher()));
        startActivity(browserIntent);
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
        private ClassMainTop mang;
        private AsyncTaskLisen lisens;
        private ClassDescriptionMang ClassDescriptionMang;
        private boolean not_net; //отвечает за проверку подклчение

        //конструктор потока
        protected Pars(AsyncTaskLisen callback,ClassMainTop mang) {
            this.mang = mang;
            this.lisens = callback;
            ClassDescriptionMang = new ClassDescriptionMang();
            ClassDescriptionMang.setNameMang(mang.getName_characher());
            ClassDescriptionMang.setImg_url(mang.getURL_img());
            not_net = false;
        }

        @Override
        protected  void  onPreExecute(){ super.onPreExecute(); }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                Log.i(PROBLEM, "StartDownload");
                if (doc == null) doc = Jsoup.connect(mang.getURL_characher()).userAgent("Mozilla")
                        .timeout(3000)
                        .get();
                Log.i(PROBLEM, "StopDownload");

                Element el = doc.select("[class = small smallText rate_info]").first();
                ClassDescriptionMang.setRank("Рейтинг:" + el.select("b").first().text());

                //считываем тома
                el = doc.select("[class = subject-meta col-sm-7]").first();
                //Получаем количетво томов
                el = el.select("p").first();
                ClassDescriptionMang.setToms(el.select("p").first().text());
           /*     el = el.nextElementSibling();
                el = el.nextElementSibling();
                //считываем
                ClassDescriptionMang.setTranslate(el.select("p").text());*/

                for (int i = 0; i < 7;i++){
                    el = el.nextElementSibling();
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
                Elements el2 = doc.select("[itemprop = description]");
                ClassDescriptionMang.setDescription(el2.attr("content"));
            } catch (IOException e) {
                e.printStackTrace();
                not_net = true;
            }catch (Exception e) {
                e.printStackTrace();
                System.out.println("Не грузит страницу либо больше нечего грузить");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if (!not_net){
                descriptionMang = ClassDescriptionMang;
                EventBus.getDefault().post(ClassDescriptionMang);
                if (lisens != null) lisens.onEnd();
                fab.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(DescriptionMang.this, "Что то с инетом", Toast.LENGTH_SHORT).show();
                DescriptionMang.this.finish();
            }
        }
    }

    public class ParsList extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i(PROBLEM, "ChapterAdd");
                el = doc.select("[class = table table-hover]").first();
                if (el != null){
                    el = el.select("tbody").first();
                    el = el.select("tr").first();
                    do {
                        parsList();
                    }while (el != null);
                }
            }catch (NullPointerException e){

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
            classForList.setName_chapter(URL);
            if (!classForList.getName_chapter().isEmpty()) arList.add(classForList);
            el = el.nextElementSibling();
        }

        @Override
        protected void onPostExecute(Void result){
            if (!arList.isEmpty()){
                classTransportForList = new ClassTransportForList(arList,mang.getName_characher(),mang);
                EventBus.getDefault().post(classTransportForList);
                Log.i(PROBLEM, "StopChapterAdd");
                if (read){
                    numberLastChapter();
                    read = false;
                }
            }
        }
    }
}
