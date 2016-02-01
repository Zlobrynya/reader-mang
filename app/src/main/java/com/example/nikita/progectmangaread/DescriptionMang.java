package com.example.nikita.progectmangaread;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
    private AdapterPargerFragment gg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple_pase);
        //установка фрагментов
        pager=(ViewPager)findViewById(R.id.pager);
        gg = new AdapterPargerFragment(getSupportFragmentManager(),2);
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
    }

    public class Pars extends AsyncTask<Void,Void,Void> {
        private String name_char,URL2;
        public ProgressDialog dialog;
        private Bitmap img;
        private AsyncTaskLisen lisens;

        //конструктор потока
        protected Pars(AsyncTaskLisen callback, classMang classMang,Context ctx) {
            this.lisens = callback;
            //progressbar пождключаем если не парсили документ
       /*     if (doc == null) {
                dialog = new ProgressDialog(ctx);
                dialog.setMessage("Загрузка...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                dialog.show();
            }*/
        }

        @Override
        protected  void  onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
       /*     try {
                if (doc == null) doc = Jsoup.connect().get();
                Element el = doc.select().first();
                for (int i =0; i < kol; i++) el = el.nextElementSibling();
                Elements el2 = el.select();
                URL2 = el2.attr("href");
                el2 = el.select();
                String imgSrc = el2.attr("src");
                name_char = el2.attr("title");
                //скачивания изображения
                InputStream inPut = new java.net.URL(imgSrc).openStream();
                //декод поток для загрузки изобр в Bitmap
                img = BitmapFactory.decodeStream(inPut);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                System.out.println("Не грузит страницу либо больше нечего грузить");
            }*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result){

        }
    }
}
