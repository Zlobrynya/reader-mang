package com.zlobrynya.project.readermang.Activity;

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

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseDownloadMang;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseViewedHead;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.cacheImage.CacheFile;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassDescriptionMang;
import com.zlobrynya.project.readermang.classPMR.ClassForList;
import com.zlobrynya.project.readermang.classPMR.ClassOtherMang;
import com.zlobrynya.project.readermang.classPMR.ClassTransportForList;
import com.zlobrynya.project.readermang.fragment.fragmentDescriptionList;
import com.zlobrynya.project.readermang.fragment.fragmentDescriptionMang;
import com.zlobrynya.project.readermang.fragment.fragmentOtherMang;
import com.zlobrynya.project.readermang.fragment.fragmentSaveDescriptionMang;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
    private Element el;
    private boolean read, downloadChapter;
    private FloatingActionButton fab1,fab2,fab3,fab;
    private Animation show_fab;
    private Animation hide_fab;
    private boolean visF = false;
    private boolean otherMang = false;
    private boolean bookmark = false;
    private boolean showDownload = false;
    private ViewPager pager;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private fragmentSaveDescriptionMang saveFragment;
    private ClassDescriptionMang classDescriptionMang;
    private ClassTransportForList classTransportForList;
    private ArrayList<ClassOtherMang> classOtherManglist;
    private final boolean DEBUG = false;

    private final String strLog = "DescripotionMang";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.description_mang, frameLayout);

     //   Log.i(PROBLEM, "Description start");
        pager = (ViewPager) findViewById(R.id.pager);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_lastChapter);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_notebook);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_download);
        show_fab = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_show);
        hide_fab = AnimationUtils.loadAnimation(getApplication(), R.anim.fab_hide);
        adapterFragment gg = new adapterFragment(getSupportFragmentManager(), 3);
        pager.setAdapter(gg);
        pager.setCurrentItem(1);
        arList = new ArrayList<>();
        read = false;

        final Intent intent = getIntent();
        showDownload = intent.getBooleanExtra("Download",false);

        if (intent.getStringExtra("URL_ch") != null){
            mang = new ClassMainTop();
            mang.setUrlCharacher(intent.getStringExtra("URL_ch"));
            mang.setNameCharacher(intent.getStringExtra("Name_ch"));
            mang.setUrlSite(intent.getStringExtra("Url_site"));
            mang.setUrlImg(intent.getStringExtra("Url_img"));
            read = intent.getBooleanExtra("read", false);
            otherMang = intent.getBooleanExtra("other", false);

            //  getSupportActionBar().setTitle(mang.getNameCharacher()); // set the top title
            parsAndSettings();
        }
        downloadChapter = false;
//        dataRecovery();

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
                    fab2.setImageResource(R.drawable.ic_favorite_white_24dp);
                    Toast.makeText(view.getContext(), "Добавлено в избранное.", Toast.LENGTH_SHORT).show();
                    bookmark = false;
                }else {
                    fab2.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Toast.makeText(view.getContext(), "Удалено из избранного.", Toast.LENGTH_SHORT).show();
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
                classDataBaseDownloadMang.addBasaData(mang.getNameCharacher());
                //добавляем в бд описание манги и т.п.
                classDataBaseDownloadMang.setData(mang.getNameCharacher(), mang.getURLCharacher(), ClassDataBaseDownloadMang.URL_MANG);
                classDataBaseDownloadMang.setData(mang.getNameCharacher(), classDescriptionMang.getRank(), ClassDataBaseDownloadMang.RATING);
                classDataBaseDownloadMang.setData(mang.getNameCharacher(), classDescriptionMang.getCategory(), ClassDataBaseDownloadMang.CATEGORY);
                classDataBaseDownloadMang.setData(mang.getNameCharacher(), classDescriptionMang.getDescription(), ClassDataBaseDownloadMang.DESCRIPTION);
                classDataBaseDownloadMang.setData(mang.getNameCharacher(), classDescriptionMang.getGenre(), ClassDataBaseDownloadMang.GENRES);
                classDataBaseDownloadMang.setData(mang.getNameCharacher(), classDescriptionMang.getNameAuthor(), ClassDataBaseDownloadMang.AUTHOR);
                classDataBaseDownloadMang.setData(mang.getNameCharacher(), classDescriptionMang.getToms(), ClassDataBaseDownloadMang.TOMS);
                classDataBaseDownloadMang.setData(mang.getNameCharacher(), classDescriptionMang.getTranslate(), ClassDataBaseDownloadMang.TRANSLATION);
              //  classDataBaseDownloadMang.closeDataBase();

                Intent newInten = new Intent(DescriptionMang.this, DownloadChapter.class);
                newInten.putExtra("mang", mang.getURLCharacher());
                newInten.putExtra("site", mang.getUrlSite());
                newInten.putExtra("Name", mang.getNameCharacher());
                newInten.putExtra("image",mang.getUrlImg());
                CacheFile file = new CacheFile(getCacheDir(), "pageCache");
                file.clearCache();
                startActivity(newInten);
                downloadChapter = true;
            }
        });
      //  dataRecovery();
       // Log.i(PROBLEM, "End onCreate");
     //   EventBus.getDefault().register(this);
    }



    //Процедура востановление
    private void dataRecovery(){
        saveFragment = (fragmentSaveDescriptionMang) getFragmentManager().findFragmentByTag("SAVE_FRAGMENT");
        if (saveFragment != null){
            classTransportForList = saveFragment.getClassTransportForList();
            classDescriptionMang = saveFragment.getClassDescriptionMang();

            if (!showDownload){
                mang = saveFragment.getMang();
                classOtherManglist = saveFragment.getClassOtherMang();
                try {
                    classDataBaseViewedHead = new ClassDataBaseViewedHead(this, mang.getNameCharacher());
                    if (classDataBaseViewedHead.getDataFromDataBase(mang.getNameCharacher(),ClassDataBaseViewedHead.NOTEBOOK).contains("1")){
                        fab2.setImageResource(R.drawable.ic_favorite_white_24dp);
                        bookmark = false;
                    }else {
                        bookmark = true;
                        fab2.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    }
                    //Если ничего не сохранено то закрываем активити
                    if (classDescriptionMang == null){
                        Toast.makeText(this, "Ошибка. Попробуйте зайти в эту мангу заново.", Toast.LENGTH_SHORT).show();
                        this.finish();
                    }
                    //    classDataBaseViewedHead = new ClassDataBaseViewedHead(this,mang.getNameCharacher());
                }catch (NullPointerException e){
                    //  Если после востановления данных вернулся false закрываем активити
                    Toast.makeText(this, "Ошибка. Попробуйте зайти в эту мангу заново.", Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                if (classOtherManglist != null)
                    EventBus.getDefault().post(classOtherManglist);
            }
            //Если ничего не сохранено то закрываем активити
            if (classDescriptionMang == null){
                Toast.makeText(this, "Ошибка. Попробуйте зайти в эту мангу заново.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
            if (classDescriptionMang != null)
                EventBus.getDefault().post(classDescriptionMang);
            if (classOtherManglist != null)
                EventBus.getDefault().postSticky(classTransportForList);
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
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onPause(){
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onResume() {
       // Log.i(strLog,"Start");

        EventBus.getDefault().register(this);

        //Посылаем данные в фрагменты
        if (classTransportForList != null){
            EventBus.getDefault().post(classTransportForList);
        }
        if (classDescriptionMang != null){
            EventBus.getDefault().post(classDescriptionMang);
        }
        if (classOtherManglist != null){
            EventBus.getDefault().post(classOtherManglist);
        }
        pager.setCurrentItem(1);
        super.onResume();
    }

    @Override
    public void onStop() {
        if (DEBUG)
            Log.i(strLog,"Stop");
        if (downloadChapter && classTransportForList != null)
            EventBus.getDefault().postSticky(classTransportForList);
        super.onStop();
    }

    @Override
    public void onStart() {
        //Вроде это должно востанавливать данные если они стерты
        // не тестированно, так что хз, востановит ли?
        if (mang == null){
            dataRecovery();
        }
        super.onStart();
    }

    AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            ParsList parsList = new ParsList();
            parsList.execute();
        }

        @Override
        public void onEnd(int number) {

        }
    };

    private void buttonVisible(){

        fab1.startAnimation(show_fab);
        fab1.setClickable(true);

        fab2.startAnimation(show_fab);
        fab2.setClickable(true);

        fab3.startAnimation(show_fab);
        fab3.setClickable(true);
    }

    private void buttonINVisble(){

        fab1.startAnimation(hide_fab);
        fab1.setClickable(false);

        fab2.startAnimation(hide_fab);
        fab2.setClickable(false);

        fab3.startAnimation(hide_fab);
        fab3.setClickable(false);

    }
    //Востанавливаем данные
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null){
            dataRecovery();
        }else{
            this.finish();
        }
        super.onRestoreInstanceState(savedInstanceState);
       // Log.d(LOG_TAG, "onRestoreInstanceState");
    }
    //Сохраняем данные
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            saveFragment.setClassDescriptionMang(classDescriptionMang);
            saveFragment.setClassTransportForList(classTransportForList);
            saveFragment.setMang(mang);
            saveFragment.setClassOtherMang(classOtherManglist);
        }catch (NullPointerException ignored){

        }
        super.onSaveInstanceState(outState);
    }

    // Получаем событие, что был клик на экран и это не фабкнопка
    // И если кнопки показаны, то их закрываем
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String event){
        if (event.contains("Click")){
            if (visF){
                buttonINVisble();
                visF = !visF;
            }
        }
    }

    public void parsAndSettings(){
       // mang = event;
        Pars pars = new Pars(addImg, mang);
        pars.execute();
        //ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle(mang.getNameCharacher()); // set the top title
        classDataBaseViewedHead = new ClassDataBaseViewedHead(this,mang.getNameCharacher());
        if (classDataBaseViewedHead.getDataFromDataBase(mang.getNameCharacher(),ClassDataBaseViewedHead.NOTEBOOK).contains("1")){
            fab2.setImageResource(R.drawable.ic_favorite_white_24dp);
            bookmark = false;
        }else {
            bookmark = true;
            fab2.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
        if (read && !otherMang)
            startLastChapter();
    }

    //ThreadMode.BACKGROUND ??
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClassDescriptionMang event){
        if (classDescriptionMang == null)
            classDescriptionMang = event;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClassTransportForList event){
        if (classTransportForList == null)
            classTransportForList = event;
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onEvent(ClassForList event){
        //узнаем нужно ли запускать активити
        if (event.getNumberChapter() >= 0){
            Intent intent = new Intent(this, ShowPages.class);
            if (!event.isDownload()){
                intent.putExtra("URL", mang.getUrlSite() + event.getURLChapter());
                intent.putExtra("NumberChapter", event.getNumberChapter());
                String helpVar = "";
                helpVar = classDataBaseViewedHead.getDataFromDataBase(mang.getNameCharacher(), ClassDataBaseViewedHead.LAST_PAGE);
                if (helpVar.isEmpty()) helpVar = "1";
                intent.putExtra("NumberPage",helpVar);
                intent.putExtra("Chapter", mang.getNameCharacher());
            }else {
                intent.putExtra("URL", event.getURLChapter());
                intent.putExtra("NumberChapter", event.getNumberChapter());
                intent.putExtra("Download",true);
                intent.putExtra("NumberPage", "1");
                intent.putExtra("Chapter", event.getNameChapter());
            }
            CacheFile file = new CacheFile(getCacheDir(), "pageCache");
            file.clearCache();
            startActivity(intent);
            EventBus.getDefault().removeStickyEvent(event);
        }
    }

    //Процедура для кнопки
   /* public void StartRead(View view) {
        startLastChapter();
    }*/

    private void startLastChapter(){
        String string = classDataBaseViewedHead.getDataFromDataBase(mang.getNameCharacher(), ClassDataBaseViewedHead.LAST_CHAPTER);
        try {
            int numberChapter = numberLastChapter();
            if (!read){
                //numberChapter = arList.size()-1;
                if (string.contains("null")){
                    ClassForList classForList = arList.get(numberChapter);
                    string = mang.getUrlSite()+classForList.getURLChapter();
                    classDataBaseViewedHead.setData(mang.getNameCharacher(), String.valueOf(numberChapter),ClassDataBaseViewedHead.VIEWED_HEAD);
                }
            }
            //Получаем дату и пишем в БД
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c.getTime());
            classDataBaseViewedHead.setData(mang.getNameCharacher(), formattedDate,ClassDataBaseViewedHead.DATA);

            if (!string.contains(mang.getUrlSite())){
                string = mang.getUrlSite() + string;
            }

            Intent intent = new Intent(this, ShowPages.class);
            intent.putExtra("URL",string);
            intent.putExtra("NumberChapter", numberChapter);
            intent.putExtra("NumberPage",classDataBaseViewedHead.getDataFromDataBase(mang.getNameCharacher(), ClassDataBaseViewedHead.LAST_PAGE));
            intent.putExtra("Chapter", mang.getNameCharacher());
            CacheFile file = new CacheFile(getCacheDir(), "pageCache");
            file.clearCache();
            startActivity(intent);
        }catch (java.lang.ArrayIndexOutOfBoundsException e){
            Toast toast = Toast.makeText(this,
                    "В манге нет глав.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private int numberLastChapter() throws ArrayIndexOutOfBoundsException{
        String string = classDataBaseViewedHead.getDataFromDataBase(mang.getNameCharacher(), ClassDataBaseViewedHead.LAST_CHAPTER);
       // Log.i("Number chapter", string);
        Short kol = 0;

        //Проверка на первый раз, если так то выдаем самую последнюю главу в списке
        if (string == null)
            return arList.size()-1;
        if (string.contains("null"))
            return arList.size()-1;

        for (ClassForList c : arList){
            String name = c.getURLChapter();
            if (DEBUG)
                Log.i("Number chapter",name);
            if (string.contains(name)) {
                if (DEBUG)
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
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mang.getURLCharacher()));
        startActivity(browserIntent);
    }

    public class adapterFragment  extends FragmentPagerAdapter {
        int kol;

        adapterFragment(FragmentManager mgr, int kol) {
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
                    return "Другая манга";
                case 1:
                    return "Описание";
                case 2:
                    return "Главы";
                default:
                    return "Magic";
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return fragmentOtherMang.newInstance();
                case 1:
                    return fragmentDescriptionMang.newInstance(position);
                default:
                    return fragmentDescriptionList.newInstance(position);
            }
        }
    }

    public class Pars extends AsyncTask<Void,Void,Void> {
        private ClassMainTop mang;
        private AsyncTaskLisen lisens;
        private ClassDescriptionMang ClassDescriptionMang;
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


            /*    if (doc == null) doc = Jsoup.connect().userAgent("Mozilla")
                        .timeout(60000)
                        .get();*/
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
           /*     el = el.nextElementSibling();
                el = el.nextElementSibling();
                //считываем
                ClassDescriptionMang.setTranslate(el.select("p").text());*/

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
                //ClassDescriptionMang.setDescription(el2.attr("content"));
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
                    getSupportActionBar().setTitle(mang.getNameCharacher()); // set the top title
                }
                classDescriptionMang = ClassDescriptionMang;
                EventBus.getDefault().post(ClassDescriptionMang);
                if (lisens != null) lisens.onEnd();
                fab.setVisibility(View.VISIBLE);
            }else{
                Toast.makeText(DescriptionMang.this, errorMassage, Toast.LENGTH_SHORT).show();
                DescriptionMang.this.finish();
            }
        }
    }

    public class ParsList extends AsyncTask<Void,Void,Void> {
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
            try{
                if (!arList.isEmpty()){
                    ClassTransportForList transportForList = new ClassTransportForList(arList,mang.getNameCharacher(),mang);
                    classTransportForList = transportForList;
                    // classDataBaseViewedHead.setData(mang.getNameCharacher(), String.valueOf(arList.size()),ClassDataBaseViewedHead.QUANTITY);
                    EventBus.getDefault().postSticky(transportForList);
                    if (read){
                        numberLastChapter();
                        read = false;
                    }
                    ParsSimilarAndRelatedMang parsList = new ParsSimilarAndRelatedMang();
                    parsList.execute();
                }
            }catch (NullPointerException e){
                Crashlytics.logException(e);
                String err = "";
                //Проверка на то, что вызывает падение
                if (mang == null)
                    err = "null";
                Crashlytics.log("Mang: " + err);
                if (arList == null)
                    err = "null";
                else err = "";
                Crashlytics.log("arList: " + err);
            }

        }
    }

    public class ParsSimilarAndRelatedMang extends AsyncTask<Void,Void,Void>{
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
