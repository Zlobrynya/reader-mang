package com.example.nikita.progectmangaread;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

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
import java.net.URL;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 01.02.2016.
 * Описание манги, нужно стьделать пролистывание фрагментов
 * 1 фрагмент описание, 2й фрагмент лист глав
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
            el = el.select("tbody").first();
            el = el.select("tr").first();
            do {
                parsList();
            }while (el != null);
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
            classDescriptionMang.setImgMang(mang.getImg_characher());
            classDescriptionMang.setNameMang(mang.getName_characher());
        }

        @Override
        protected  void  onPreExecute(){ super.onPreExecute(); }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                //Потом как нить переделать этот парсер
                if (doc == null) doc = Jsoup.connect(mang.getURL_characher()).get();

                Element el = doc.select("[class = small smallText rate_info]").first();
                classDescriptionMang.setRank(el.select("b").first().text());

                el = doc.select("[class = subject-meta col-sm-7]").first();
                el = el.select("p").first();
                classDescriptionMang.setToms(el.select("p").first().text());
                el = el.nextElementSibling();
                el = el.nextElementSibling();
                classDescriptionMang.setTranslate(el.select("p").text());

                el = doc.select("[class = elementList]").first();
                Elements el4 = el.select("span");
                el4 = el4.select("a");
                classDescriptionMang.setGenre(el4.text());
                el = el.nextElementSibling();
                el = el.nextElementSibling();
                classDescriptionMang.setNameAuthor(el.text());

                Elements el2 = doc.select("[class = leftContent]");
                //описание выбора http://jsoup.org/apidocs/org/jsoup/select/Selector.html
                el2 = doc.select("[itemprop = description]");
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
