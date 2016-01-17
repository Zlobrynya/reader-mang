package com.example.nikita.progectmangaread;

import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 13.01.2016.
 */

public class fragmentTemplePase extends Fragment {
    public classMang classMang;
    public int kol,kolSum,totalSum,firstItem,itemCount;
    public Document doc;
    public ArrayList<MainClassTop> list;
    public AdapterMainScreen myAdap;
    public GridView gr;
    private boolean parse;
    private int pageNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        myAdap = new AdapterMainScreen(getActivity(),R.layout.layout_from_graund_view,list);
    }


    //Разобраться с сохранением переменных при переключении
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        kol = 0;
        kolSum = 24;
        parse = true;
        View v = inflater.inflate(R.layout.fragment, null);
        pageNumber = this.getArguments().getInt("num");
        gr = (GridView) v.findViewById(R.id.gread_id);
        gr.setAdapter(myAdap);
        return v ;
    }

    //создается клас с описанием интерфейсв
    AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            loadimg();
        }
    };

    public void onEvent(classMang event){
        if (event.getNumberPage() == pageNumber) {
            classMang = event;
            parssate();
        }
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

    //фабричный метод для ViewPager
    public static fragmentTemplePase newInstance(int page) {
        fragmentTemplePase fragment = new fragmentTemplePase();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    //метод парсим
    public void parssate(){
        //парсим сайт
        Pars past = new Pars(addImg,kol,classMang);
        past.execute();
    }

    //слушатели gridview
    public void loadimg(){
        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainClassTop Class;
                Class = list.get(position);
                Toast toast = Toast.makeText(getActivity(),
                        Class.getURL_characher(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        gr.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (firstItem + itemCount >= totalSum && kol <= classMang.getMaxInPage()) {
                    kolSum += 4;
                    kol++;
                    parssate();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                totalSum = totalItemCount;
                firstItem = firstVisibleItem;
                itemCount = visibleItemCount;

            }
        });
        if (kol < kolSum && kol <= classMang.getMaxInPage() && parse) {
                    kol++;
                    parssate();
        }
    }


    //Нужно сделать остановку парсера при перелистывании на другую страницу
    //!!!!!!!!!!!!!!!!!!
    public class Pars extends AsyncTask<Void,Void,Void> {
        private String URL,URL2;
        private classMang classMang;
        private Bitmap img;
        private AsyncTaskLisen lisens;
        private int kol;

        //конструктор потока
        protected Pars(AsyncTaskLisen callback, int kol,classMang classMang) {
            this.lisens = callback;
            this.kol = kol;
            this.classMang = classMang;
        }

        @Override
        protected  void  onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                if (doc == null) doc = Jsoup.connect(classMang.getUML() + classMang.getWhere()).get();
                Element el = doc.select(classMang.getNameCell()).first();
                for (int i =0; i < kol; i++) el = el.nextElementSibling();
                Elements el2 = el.select(classMang.getNameUML());
                URL2 =el2.attr("href");
                el2 = el.select(classMang.getImgUML());
                String imgSrc = el2.attr("src");
                //скачивания изображения
                InputStream inPut = new java.net.URL(imgSrc).openStream();
                //декод поток для загрузки изобр в Bitmap
                img = BitmapFactory.decodeStream(inPut);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                System.out.println("Не грузит страницу либо больше нечего грузить");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //добавляем в лист и обновление
            if (img != null) {
                MainClassTop a = new MainClassTop(img, URL2);
                if (kol > myAdap.getCount()){
                    System.out.println("NumberPage: " + pageNumber + " kol: " + kol + " kolSum: " + kolSum + " sizeAdapter: " + myAdap.getCount());
                    myAdap.add(a);
                    myAdap.notifyDataSetChanged();
                }
                //кричим интерфейсу что мы фсе
                if (lisens != null) lisens.onEnd();
            }
        }
    }


}
