package com.example.nikita.progectmangaread;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterSlidingMenu;
import com.example.nikita.progectmangaread.DataBasePMR.DataBaseViewedHead;
import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseViewedHead;
import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classDescriptionMang;
import com.example.nikita.progectmangaread.classPMR.classForList;
import com.example.nikita.progectmangaread.classPMR.classTransportForList;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionList;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionMang;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

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

public class DescriptionMang extends AppCompatActivity {
    public Document doc;
    private MainClassTop mang;
    public ArrayList<classForList> arList;
    public int kol;
    private ViewPager pager;
    private adapterFragment gg;
    private Pars pars;
    private Element el;
    private SlidingMenu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kol = 0;
        setContentView(R.layout.activity_temple_pase);
        pager=(ViewPager)findViewById(R.id.pager);
        gg = new adapterFragment(getSupportFragmentManager(),2);
        pager.setAdapter(gg);
        arList = new ArrayList<classForList>();
        EventBus.getDefault().register(this);
        // <------------------------------------------------------->
        //      Sliding Menu
        final Context context = this;
        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(R.layout.sliding_menu, null, true);
        final ListView lv=(ListView) v.findViewById(R.id.listView);

        menu = new SlidingMenu(this);
        AdapterSlidingMenu ma = new AdapterSlidingMenu(context);
        lv.setAdapter(ma);

        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.1f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(v);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                      @Override
                                      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                          SlidingMenuConstant constant = null;
                                          switch (position){
                                              case 0: constant = SlidingMenuConstant.LIST_MANG;
                                                  break;
                                              case 1: constant = SlidingMenuConstant.FAVORITES;
                                                  break;
                                              case 2: constant = SlidingMenuConstant.DOWLAND;
                                                  break;
                                              default:
                                                  constant = SlidingMenuConstant.MAGIС;
                                          }
                                          EventBus.getDefault().post(constant);
                                      }
                                  }

        );

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // <-------------------------------------------------------------------------->

    }

    //Метод для открытия бокового меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {  // узнаем ID нажатой кнопки
            case android.R.id.home: // если это кнопка-иконка ActionBar,
                menu.toggle(true);        // открываем меню (или закрываем)
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

    //ПОЛУЧАЕМ с топлиста
    public void onEvent(MainClassTop event){
        mang = event;
        pars = new Pars(addImg,mang);
        pars.execute();
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle(event.getName_characher()); // set the top title
    }

    public void onEvent(classForList URL){
        //узнаем нужно ли запускать активити
        if (URL.getNumberChapter() >= 0){
            Intent intent = new Intent(this, pagesDownload.class);
            intent.putExtra("URL", mang.getURL_site()+URL.getURL_chapter());
            intent.putExtra("NumberChapter", URL.getNumberChapter());

            classDataBaseViewedHead classDataBaseViewedHead = new classDataBaseViewedHead(this,mang.getName_characher());
            String string = classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), DataBaseViewedHead.LAST_CHAPTER);
            int numberPage = 0;
            if (!string.contains("null")){
                String[] strings = string.split(",");
                String f = URL.getName_chapter();
                if (strings[0].contains(String.valueOf(URL.getNumberChapter()))) {
                    if (strings.length == 2) {
                        numberPage = Integer.parseInt(strings[1]);
                    }
                }
            }

            intent.putExtra("NumberPage", numberPage);
            intent.putExtra("Chapter", mang.getName_characher());
            startActivity(intent);
        }
    }
    //Процедура для кнопки
    public void StartRead(View view) {
        classDataBaseViewedHead classDataBaseViewedHead = new classDataBaseViewedHead(this,mang.getName_characher());
        String string = classDataBaseViewedHead.getDataFromDataBase(mang.getName_characher(), DataBaseViewedHead.LAST_CHAPTER);
        int numberPage = 0;
        int numberChapter = arList.size()-1;
        if (!string.contains("null")){
            String[] strings = string.split(",");
            numberChapter = Integer.parseInt(strings[0]);
            if (strings.length == 2){
                numberPage = Integer.parseInt(strings[1]);
            }
        }
        classForList classForList = arList.get(numberChapter);
        Intent intent = new Intent(this, pagesDownload.class);
        intent.putExtra("URL",mang.getURL_site()+classForList.getURL_chapter());
        intent.putExtra("NumberChapter", numberChapter);
        intent.putExtra("NumberPage", numberPage);
        intent.putExtra("Chapter", mang.getName_characher());
        startActivity(intent);
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
            classTransportForList classTransportForList = new classTransportForList(arList,mang.getName_characher());
            EventBus.getDefault().post(classTransportForList);
        }
    }
}
