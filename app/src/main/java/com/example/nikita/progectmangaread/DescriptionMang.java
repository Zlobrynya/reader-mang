package com.example.nikita.progectmangaread;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 01.02.2016.
 * Описание манги, нужно стьделать пролистывание фрагментов
 * 1 фрагмент описание, 2й фрагмент лист глав
 */

public class DescriptionMang extends AppCompatActivity {
    public Document doc;
    private MainClassTop mang;
    private ViewPager pager;
    private adapterFragment gg;
    private Pars pars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple_pase);
        pager=(ViewPager)findViewById(R.id.pager);
        gg = new adapterFragment(getSupportFragmentManager(),2);
        pager.setAdapter(gg);

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

    public void onEvent(MainClassTop event){
        mang = event;
        pars = new Pars(this,mang);
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
            return fragmentDescriptionMang.newInstance(position);
        }
    }


    public class Pars extends AsyncTask<Void,Void,Void> {
        private ProgressDialog dialog;
        private MainClassTop mang;
        private Bitmap img;
        private classDescriptionMang classDescriptionMang;

        //конструктор потока
        protected Pars(Context ctx,MainClassTop mang) {
            this.mang = mang;
            classDescriptionMang = new classDescriptionMang();
            classDescriptionMang.setImgMang(mang.getImg_characher());
            classDescriptionMang.setNameMang(mang.getName_characher());
            //progressbar пождключаем если не парсили документ
            if (doc == null) {
                dialog = new ProgressDialog(ctx);
                dialog.setMessage("Загрузка...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                dialog.show();
            }
        }

        @Override
        protected  void  onPreExecute(){
            super.onPreExecute();
        }

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
         //       translate = el.select("p").text();

                el = doc.select("[class = elementList]").first();
                Elements el4 = el.select("span");
                el4 = el4.select("a");
                classDescriptionMang.addGenre(el4.text());
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
            if (dialog != null) dialog.dismiss();
            EventBus.getDefault().post(classDescriptionMang);

        }
    }


}
