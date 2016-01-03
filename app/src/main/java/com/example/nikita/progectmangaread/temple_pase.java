package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class temple_pase extends AppCompatActivity {

    public String URL;
    public String nameCell,nameURL,nameIMG,where;
    public int kol,kolSum,poss;
    public Document doc;
    public ArrayList<MainClassTop> list;
    private float moveY;
    public GridView gr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple_pase);
        kol = 0;
        kolSum = 20;
        list = new ArrayList<>();
        poss = 0;
        //получение данных из главной активити
        URL = "";
        //получаем ссылку на адрес
        URL = getIntent().getExtras().getString("URL");
        nameCell = "";
        //получаем имя ячейки таблицы
        nameCell = getIntent().getExtras().getString("Cell");
        //получаем имя тега URL манги
        nameURL = getIntent().getExtras().getString("nameURL");
        //каталог
        where = getIntent().getExtras().getString("Where");
        //получаем имя тега IMG манги
        nameIMG = getIntent().getExtras().getString("nameIMG");
        //грузим страницу html
        parssate();
    }

    //метод парсим
    public void parssate(){
        //создается клас с описанием интерфейсв
        AsyncTaskLisen addImg = new AsyncTaskLisen() {
            @Override
            public void onEnd() {
                loadimg();
            }
        };
        //парсим сайт

        Pars past = new Pars(addImg,kol,URL);
        past.execute();
      //  gr.smoothScrollToPosition(poss);
    }

    //обнова экрана
    public void loadimg(){
        gr = (GridView)findViewById(R.id.gread_id);
        AdapterMainScreen myAdap = new AdapterMainScreen(this,R.layout.layout_from_graund_view,list);
        gr.setAdapter(myAdap);
        gr.smoothScrollToPosition(poss);
        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainClassTop Class;
                Class = list.get(position);

                Toast toast = Toast.makeText(getApplicationContext(),
                        Class.getURL_characher(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        poss = gr.getFirstVisiblePosition();
        gr.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                 System.out.println(view.getFirstVisiblePosition());
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        //закгружается пока не загрузится 13 картинок
        if (kol < kolSum) {
            //if (poss == 6) gr.smoothScrollToPosition(poss);
            kol++;
           // System.out.println("GG");
            parssate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }



    public class Pars extends AsyncTask<Void,Void,Void> {
        private String URL,URL2;
        private Bitmap img;
        private AsyncTaskLisen lisens;
        private int kol;

        //конструктор потока
        protected Pars(AsyncTaskLisen callback, int kol,String URL) {
            this.lisens = callback;
            this.kol = kol;
            this.URL = URL;
        }


        @Override
        protected  void  onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                if (doc == null) doc = Jsoup.connect(URL+where).get();
                Element el = doc.select(nameCell).first();
                for (int i =0; i < kol; i++) el = el.nextElementSibling();
                Elements el2 = el.select(nameURL);
                URL2 =el2.attr("href");
                el2 = el.select(nameIMG);
                String imgSrc = el2.attr("src");
                //скачивания изображения
                InputStream inPut = new java.net.URL(imgSrc).openStream();
                //декод поток для загрузки изобр в Bitmap
                img = BitmapFactory.decodeStream(inPut);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //добавляем в лист
            list.add(new MainClassTop(img,URL2));
            //кричим интерфейсу что мы фсе
            if (lisens != null) lisens.onEnd();
        }
    }

}
