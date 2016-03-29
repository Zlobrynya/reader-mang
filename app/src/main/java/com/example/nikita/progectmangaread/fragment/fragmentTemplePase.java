package com.example.nikita.progectmangaread.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 13.01.2016.
 * Класс фрагмента где выводится таблицей топ манг, с возможностью тыканья в них
 * ---
 * Сделать:
 * и с помощью сервиса обновлять из раз в неделю (пункт дaлеко идущего плана)
 *
 * Переделать под новую библиотеку!
 *
 * ---
 */

public class fragmentTemplePase extends Fragment {
    public com.example.nikita.progectmangaread.classPMR.classMang classMang;
    private int firstItem,height,width,page;
    private int kol,kolSum,kolSum_previous,kol_previous;
    public int lastItem,kolImage;
    public Document doc;
    public LinkedList<MainClassTop> list;
    public AdapterMainScreen myAdap;
    private GridView gr;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSqLiteDatabase;
    public boolean mIsScrollingUp,search_and_genres,resultPost;
    private Pars past;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new LinkedList<>();
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
        mIsScrollingUp = search_and_genres = false;
        kolSum = 28;
       // kolSum = 8;
        kolSum_previous = 0;
        kol_previous = kolImage = 0;
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
            //непомню как работает
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int firstVisibleItem = gr.getFirstVisiblePosition();
                int lastVisibleItem = gr.getLastVisiblePosition();
                if (firstItem != firstVisibleItem) {
                    if (firstItem > firstVisibleItem) {
                        mIsScrollingUp = true; //UP /\
                        //     kol = lastVisibleItem;
                        kolSum_previous = lastVisibleItem - 30;
                        if (kol_previous < 0) kolSum_previous = 0;
                        Log.i("Scroll 1:", "Up");
                    }
                    if (firstItem < firstVisibleItem) {
                        mIsScrollingUp = false; //DOWN \/
                        // kol = firstVisibleItem;
                        kolSum = firstVisibleItem + 30;
                        if (kolSum > kol)
                            parssate(kol);
                        Log.i("Scroll 1:", "Down, kolSum " + kolSum + " kol: " + kol);
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
        if (resultPost) parssate(kol);
        System.out.println("!! " + width + " " + height + 5);
        return v ;
    }

    private void initializationArray(){
        while (!download_the_html(kol,0)){
            list.add(getMainClassTop(kol,0));
            kol++;
        }
        if (kol == 0) parssate(kol);
        myAdap.notifyDataSetChanged();
    }

    //создается клас с описанием интерфейсв
    AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            if (kol < kolSum) {
                kol++;
                parssate(kol);
            }
        }

        @Override
        public void onEnd(InputStream is) {

        }
    };

    public void onEvent(classMang event){
        classMang = event;
        //parssate();
        //создание базы данных
        String nameBase = classMang.getUML().replace(".me", ".db");
        nameBase = nameBase.replace("http://","");
        nameBase = nameBase.replace(".ru",".db");
        mDatabaseHelper = new DatabaseHelper(getActivity(),nameBase, null, 1);
        SQLiteDatabase sdb;
        sdb = mDatabaseHelper.getReadableDatabase();
        mSqLiteDatabase = mDatabaseHelper.getWritableDatabase();
        initializationArray();
    }

    //Для фрагментов
    public void add(classTransport ev) {
        classMang = ev.getClassMang();
        classMang.setWhere(ev.getURL_Search());
        resultPost = true;
    }

        @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        if (past != null) past.cancel(false);
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
        past = new Pars(addImg,kol,classMang,getContext());
        past.execute();
    }

    //добавление в базу данных
    void addBasaData(MainClassTop a, String imgSrc){
        String query,name;
        name = "\"";
        name += a.getName_characher().replace('"',' ') + "\"";
        query = "SELECT " + DatabaseHelper.NAME_MANG + " FROM " + DatabaseHelper.DATABASE_TABLE + " WHERE " + DatabaseHelper.NAME_MANG + "=" +
                name;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor.getCount() == 0){
            ContentValues newValues = new ContentValues();
            // Задайте значения для каждого столбца
            newValues.put(DatabaseHelper.NAME_MANG, a.getName_characher().replace('"', ' '));
            newValues.put(DatabaseHelper.URL_CHAPTER, a.getURL_characher());
            newValues.put(DatabaseHelper.URL_IMG, imgSrc);
            // Вставляем данные в таблицу
            mSqLiteDatabase.insert("Mang", null, newValues);
        }
        cursor.close();
    }

    //получаем структуру с именем и сылками
    MainClassTop getMainClassTop(int kol,int page){
        String query = "SELECT " + "*" + " FROM " + DatabaseHelper.DATABASE_TABLE + " WHERE " + " _id" + "=" +
                (kol+1);
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);

        if (cursor.getCount() != 0){
            MainClassTop a = new MainClassTop();
            cursor.moveToFirst();
            a.setURL_img(cursor.getString(cursor.getColumnIndex(DatabaseHelper.URL_IMG)));
            a.setName_characher(cursor.getString(cursor.getColumnIndex(DatabaseHelper.NAME_MANG)));
            a.setURL_characher(cursor.getString(cursor.getColumnIndex(DatabaseHelper.URL_CHAPTER)));
            cursor.close();
            return a;
        }
        cursor.close();
        return null;
    }

    //проверяем в бд есть ли в такой элемент
    Boolean download_the_html(int kol,int page){
        String query = "SELECT " + "*" + " FROM " + DatabaseHelper.DATABASE_TABLE + " WHERE " + " _id" + "=" +
                (kol+1);
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        //Log.i("LOG_TAG", "download_the_html " + cursor.getCount());
        if (cursor.getCount() == 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public class Pars extends AsyncTask<Void,Void,Void> {
        private String name_char,URL2;
        private classMang classMang;
        public ProgressDialog dialog;
        private Bitmap img;
        private AsyncTaskLisen lisens;
        private String imgSrc;
        private int kol,l;
        private Boolean down;

        //конструктор потока
        protected Pars(AsyncTaskLisen callback, int kol,classMang classMang,Context ctx) {
            this.lisens = callback;
            this.kol = kol;
            this.classMang = classMang;
            down = false;
        }

        @Override
        protected  void  onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                page = kol / classMang.getMaxInPage();
//                   page = kol / classMang.getMaxInPage();
                int kol_mang = kol - (classMang.getMaxInPage()*page);
                if (doc == null) {
                    classMang.editWhere(page);
                    doc = Jsoup.connect(classMang.getUML() + classMang.getWhere()).get();
                    down = false;
                }

                Element el = doc.select(classMang.getNameCell()).first();
                for (int i = 0; i < kol_mang; i++)
                    el = el.nextElementSibling();
                Elements el2 = el.select(classMang.getNameUML());

                URL2 = classMang.getUML() + el2.attr("href");
                el2 = el.select(classMang.getImgUML());

                imgSrc = el2.attr("src");
                name_char = el2.attr("title");
                if (kol_mang == 69) doc = null;
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
            //добавляем в лист и обновление
            if (kol >= 0) {
                MainClassTop a = new MainClassTop(URL2,name_char,imgSrc);
                Log.i("Kol parse: ", String.valueOf(kol));
                if (list.size() <= kol) list.add(kol,a);
                if (!resultPost) {
                    addBasaData(a, imgSrc);
                }
                myAdap.notifyDataSetChanged();
                //кричим интерфейсу что мы фсе
                if (lisens != null) lisens.onEnd();
            }
        }
    }
}
