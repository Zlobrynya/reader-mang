package com.example.nikita.progectmangaread.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterMainScreen;
import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.DataBasePMR.DatabaseHelper;
import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.classMang;
import com.example.nikita.progectmangaread.classPMR.classTransport;
import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseListMang;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 13.01.2016.
 * Класс фрагмента где выводится таблицей топ манг, с возможностью тыканья в них
 * ---
 * Сделать:
 * и с помощью сервиса обновлять из раз в неделю (пункт дaлеко идущего плана)
 * СДЕЛАТЬ:
 * Переделать под новую библиотеку!
 * Продумать обовление что бы не "моргало"
 * Убрать лишнее 
 *
 * ---
 */

public class fragmentTemplePase extends Fragment {
    public com.example.nikita.progectmangaread.classPMR.classMang classMang;
    private int firstItem,height,width,page;
    private int kol,kolSum;
    public int lastItem,kolImage,summ;
    private Document doc;
    private LinkedList<MainClassTop> list;
    private AdapterMainScreen myAdap;
    private classDataBaseListMang classDataBaseListMang;
    private GridView gr;
    private boolean stopLoad;
    // 0 - глав стр 1 - результат поиска 2 - по жанрам
    private int resultPost;
    private Pars past;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new LinkedList<>();
        stopLoad = false;
        //для узнавания разрешения экрана
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;
        //создаем адаптер для GriedView
        //result = false;
        kol = page = lastItem = firstItem = 0;
        myAdap = new AdapterMainScreen(getActivity(), R.layout.layout_from_graund_view,list,width,height);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        kolSum = 28;
        summ = kolImage = 0;
        View v = inflater.inflate(R.layout.fragment, null);

        gr = (GridView) v.findViewById(R.id.gread_id);
        gr.setAdapter(myAdap);

        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainClassTop Class = list.get(position);
                //отправляем в DescriptionMang
                EventBus.getDefault().post(Class);
            }
        });

        gr.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int firstVisibleItem = gr.getFirstVisiblePosition();
                int lastVisibleItem = gr.getLastVisiblePosition();
                if (firstItem != firstVisibleItem) {
                    if (firstItem < firstVisibleItem) {
                        kolSum += 10;
                        if (kolSum > kol && !stopLoad)
                            parssate(kol);
                        //  Log.i("Scroll 1:", "Down, kolSum " + kolSum + " kol: " + kol);
                    }
                }
                firstItem = firstVisibleItem;
                lastItem = lastVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Log.i("Scroll 2", String.valueOf(helpVasr));
            }
        });
        return v ;
    }

    private void initializationArray(){
        while (!classDataBaseListMang.download_the_html(kol)){
            MainClassTop classTop = classDataBaseListMang.getMainClassTop(kol);
            classTop.setURL_site(classMang.getURL());
            list.add(classTop);
            kol++;
            myAdap.notifyDataSetChanged();
        }
        resultPost = 0;
        if (kol == 0) parssate(kol);
    }

    //создается клас с описанием интерфейсв
    AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            if (kol < kolSum && !stopLoad) {
                kol++;
                parssate(kol);
            }
        }

        @Override
        public void onEnd(InputStream is) {

        }
    };

    public void onEvent(classMang event){
        if (classMang != null){
            if (!event.getURL().contains(classMang.getURL())){
                list.clear();
                kol = 0;
                page = 0;
                doc = null;
            }
        }
        classMang = event;
        //создание базы данных
        String nameTable = classMang.getURL().replace(".me", " ");
        nameTable = nameTable.replace("http://"," ");
        nameTable = nameTable.replace(".ru", " ");
        classDataBaseListMang = new classDataBaseListMang(getContext(),nameTable);
        initializationArray();
    }

    //Для фрагментов
    public void add(classTransport ev) {
        classMang = ev.getClassMang();
        classMang.setWhere(ev.getURL_Search());
        if (ev.getURL_Search().contains("search")) resultPost = 1;
        else resultPost = 2;
        parssate(kol);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop(){
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
    public void parssate(int kol){
        //парсим сайт
        past = new Pars(addImg,kol,classMang);
        past.execute();
    }

    public class Pars extends AsyncTask<Void,Void,Void> {
        private String name_char,URL2;
        private classMang classMang;
        private AsyncTaskLisen lisens;
        private String imgSrc;
        private int kol;

        //конструктор потока
        protected Pars(AsyncTaskLisen callback, int kol,classMang classMang) {
            this.lisens = callback;
            this.kol = kol;
            this.classMang = classMang;
            URL2 = name_char = imgSrc = "";
        }

        @Override
        protected  void  onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                int kol_mang;
                // Проверка на то что сейчас парсим, если запрос то там без страниц идет
                if (resultPost != 1){
                    page = kol / classMang.getMaxInPage();
                    kol_mang = kol - (classMang.getMaxInPage()*page);
                }else kol_mang = kol;

                if (doc == null) {
                    classMang.editWhere(page);
                    // Подключаемся и качаем html страницу
                    doc = Jsoup.connect(classMang.getURL() + classMang.getWhereAll()).get();
                }

                Element el = doc.select(classMang.getNameCell()).first();
                for (int i = 0; i < kol_mang; i++)
                    el = el.nextElementSibling();
                Elements el2 = el.select(classMang.getNameURL());

                URL2 = classMang.getURL() + el2.attr("href");
                el2 = el.select(classMang.getImgURL());

                imgSrc = el2.attr("src");
                name_char = el2.attr("title");
                summ++;
                if (kol_mang == 69 && resultPost != 1) doc = null;
            } catch (IOException e) {
                e.printStackTrace();
            }catch (Exception e) {
                System.out.println("Не грузит страницу либо больше нечего грузить");
                stopLoad = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //добавляем в лист и обновление
            if (kol >= 0 && !URL2.isEmpty() && !name_char.isEmpty() && !classMang.getURL().isEmpty()) {
                MainClassTop a = new MainClassTop(URL2,name_char,imgSrc,classMang.getURL());
                Log.i("Kol parse: ", String.valueOf(kol));
                try {
                    if (list.size() <= kol)
                        list.add(a);
                    if (resultPost == 0) {
                        classDataBaseListMang.addBasaData(a,kol);
                    }
                    myAdap.notifyDataSetChanged();

                }catch (IndexOutOfBoundsException e){
                    Log.i("Error: ",e.toString());
                    Log.i("Size list: ", String.valueOf(list.size()));
                    Log.i("Kol: ", String.valueOf(kol));
                }
                //кричим интерфейсу что мы фсе
                if (lisens != null) lisens.onEnd();
            }
        }
    }
}
