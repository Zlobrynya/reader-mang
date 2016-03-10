package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classDescriptionMang;
import com.example.nikita.progectmangaread.classPMR.classForList;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionList;
import com.example.nikita.progectmangaread.fragment.fragmentDescriptionMang;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 01.02.2016.
 * Описание манги, нужно стьделать пролистывание фрагментов
 * 1 фрагмент описание, 2й фрагмент лист глав
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kol = 0;
        setContentView(R.layout.activity_temple_pase);
        pager=(ViewPager)findViewById(R.id.pager);
        gg = new adapterFragment(getSupportFragmentManager(),2);
        pager.setAdapter(gg);
        arList = new ArrayList<classForList>();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            el = doc.select("[class = table table-hover]").first();
            if (el != null){
                el = el.select("tbody").first();
                el = el.select("tr").first();
                do {
                    parsList();
                }while (el != null);
            }

        }

        @Override
        public void onEnd(InputStream is) {

        }
    };

    void parsList(){
        classForList classForList = new classForList();
        Elements el2 = el.select("a");
        String URL = el2.attr("href");
        classForList.setURL_chapter(URL);
        URL = el2.select("a").text();
        classForList.setName_chapter(URL);
        EventBus.getDefault().post(classForList);
        el = el.nextElementSibling();
    }


    public void onEvent(MainClassTop event){
        mang = event;
        pars = new Pars(addImg,mang);
        pars.execute();
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle(event.getName_characher()); // set the top title
    }

    public void onEvent(String URL){
        Intent intent = new Intent(this, pagesDownload.class);
        intent.putExtra("URL",URL);
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
}
