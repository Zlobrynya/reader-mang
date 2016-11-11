package com.example.nikita.progectmangaread.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.Activity.DescriptionMang;
import com.example.nikita.progectmangaread.AdapterPMR.AdapterMainScreen;
import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.classPMR.ClassMainTop;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassMang;
import com.example.nikita.progectmangaread.classPMR.ClassTransport;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseListMang;
import com.example.nikita.progectmangaread.Activity.TopManga;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Nikita on 13.01.2016.
 * Класс фрагмента где выводится таблицей топ манг, с возможностью тыканья в них
 * ---
 * Сделать:
 * и с помощью сервиса обновлять из раз в неделю (пункт дaлеко идущего плана)
 *
 * ---
 */

public class fragmentTopMang extends Fragment {
    private ClassMang classMang;
    private int firstItem,page;
    private int kol,kolSum;
    private Document doc;
    private LinkedList<ClassMainTop> list;
    private AdapterMainScreen myAdap;
    private ClassDataBaseListMang classDataBaseListMang;
    private GridView gr;
    private boolean stopLoad,visibleButton;
    private int resultPost;  // 0 - глав стр 1 - результат поиска 2 - по жанрам
    private ClassMainTop mainTop;


    public void clearData() {
        classMang = null;
        mainTop = null;
        doc = null;
        if (list != null){
            list.clear();
            myAdap.notifyDataSetChanged();
        }
        kol = page = firstItem = 0;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new LinkedList<>();
        stopLoad = false;
        visibleButton = true;
        kol = page = firstItem = kolSum = 0;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int height,width;
        //для узнавания разрешения экрана
        height = TopManga.HEIGHT_WIND/sizeCalculate(TopManga.HEIGHT_WIND);
        width = TopManga.WIDTH_WIND/sizeCalculate(TopManga.WIDTH_WIND);

       // Log.i("W,H TemplePase",height + " " + width);

        //создаем адаптер для GriedView
        myAdap = new AdapterMainScreen(getActivity(), R.layout.layout_from_graund_view,list,width,height);

        if (kolSum == 0){
            switch (sizeCalculate(TopManga.WIDTH_WIND)){
                case 4: kolSum = 28;
                    break;
                case 6: kolSum = 40;
                    break;
                case 15: kolSum = 150;
                    break;
                default: kolSum = 90;
            }
        }

        View v = inflater.inflate(R.layout.fragment_top_manga, null);

        gr = (GridView) v.findViewById(R.id.gread_id);
        gr.setAdapter(myAdap);
        gr.setNumColumns(sizeCalculate(TopManga.WIDTH_WIND));

        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainTop = list.get(position);
                //отправляем в DescriptionMang
                Intent intent = new Intent(getActivity(),DescriptionMang.class);
                intent.putExtra("URL_ch",mainTop.getURLCharacher());
                intent.putExtra("Url_img",mainTop.getUrlImg());
                intent.putExtra("Name_ch",mainTop.getNameCharacher());
                intent.putExtra("Url_site",mainTop.getUrlSite());
                startActivity(intent);
            //    Log.i(PROBLEM, "click item");
            }
        });

        final FloatingActionButton upButton = (FloatingActionButton) v.findViewById(R.id.skip_to_top);
        final Animation fabShow = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_show);
        final Animation fabHide = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_hide);
        upButton.setVisibility(View.INVISIBLE);
        upButton.setClickable(false);
        visibleButton = false;


        gr.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int firstVisibleItem = gr.getFirstVisiblePosition();
                if (firstItem <= firstVisibleItem) {
                    kolSum += 10;
                    if (kolSum > kol && !stopLoad)
                        parssate(kol);
                    if (visibleButton){
                        upButton.setAnimation(fabHide);
                        upButton.setClickable(false);
                        visibleButton = false;
                    }
                    //  Log.i("Scroll 1:", "Down, kolSum " + kolSum + " kol: " + kol);
                }else {
                    if (!visibleButton  && firstVisibleItem != 0){
                        upButton.setAnimation(fabShow);
                        upButton.setClickable(true);
                        visibleButton = true;
                    }
                }
                if (firstVisibleItem == 0){
                    if (visibleButton){
                        upButton.setAnimation(fabHide);
                        upButton.setClickable(false);
                        visibleButton = false;
                    }
                }
                firstItem = firstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Log.i("Scroll 2", String.valueOf(helpVasr));
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gr.smoothScrollToPosition(0);
            }
        });


        return v ;
    }

    //расчитать количество столбцов в строке
    private int sizeCalculate(double size){
        //Портретная ориентация
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            if (size <= 720) return 4;
            if (size >= 720 && size <= 1500) return 6;
            if (size >= 1500) return 8;
        }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (size <= 720) return 3;
            if (size >= 720 && size <= 1500) return 6;
            if (size >= 1500) return 8;
            if (size >= 2000) return 12;
        }
        return 3;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) kolSum = kol + 10;
        Log.i("temp Pase","result");
        //Log.i(PROBLEM, "onActivityCreated");

    }

    //создается клас с описанием интерфейсв
    AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            if (kol < kolSum && !stopLoad) {
                Log.i("Lisener", String.valueOf(kol)+" "+kolSum);
                kol++;
                parssate(kol);
            }
        }

        @Override
        public void onEnd(int number) {
            if (list.isEmpty() ){
                TextView textView = (TextView) getActivity().findViewById(R.id.text_top_mang_info);
                textView.setVisibility(View.VISIBLE);
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClassMang event){
        if (classMang != null){
            if (!event.getURL().contains(classMang.getURL())){
                list.clear();
                kol = 0;
                page = 0;
                doc = null;
            }
        }
        if (event == null)
            return;
        classMang = event;
        //создание базы данных
        String nameTable = classMang.getURL().replace(".me", " ");
        nameTable = nameTable.replace("http://"," ");
        nameTable = nameTable.replace(".ru", " ");
        classDataBaseListMang = new ClassDataBaseListMang(getContext(),nameTable);
        InitializationArray array = new InitializationArray();
        array.execute();
        // Log.i(PROBLEM, "onEvent(ClassMang event)");
    }

    //Для фрагментов
    public void add(ClassTransport ev) {
        classMang = ev.getClassMang();
        classMang.setWhere(ev.getURL_Search());
        if (ev.getURL_Search().contains("search")) resultPost = 1;
        else resultPost = 2;
        parssate(kol);
     //   Log.i(PROBLEM, "add(ClassTransport ev)");
    }



    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop(){
      //  Log.i(PROBLEM, "Stop fragment Top Manga");
        if (mainTop != null)
            EventBus.getDefault().post(mainTop);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy(){
       // Log.i(PROBLEM, "onDestroy");
       /* if (classDataBaseListMang != null)
            classDataBaseListMang.closeDataBase();*/
        super.onDestroy();
    }

    //фабричный метод для ViewPager
    public static fragmentTopMang newInstance(int page) {
        fragmentTopMang fragment = new fragmentTopMang();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    //метод парсим
    public void parssate(int kol){
        //парсим сайт
        Pars past = new Pars(addImg, kol, classMang);
        past.execute();
    }

    //Перенос возможен в отдельный пакет.
    public class Pars extends AsyncTask<Void,Void,Void> {
        private String nameChar,URL2;
        private ClassMang classMang;
        private AsyncTaskLisen lisens;
        private String imgSrc;
        private int kol;
        private boolean not_net;

        //конструктор потока
        Pars(AsyncTaskLisen callback, int kol, ClassMang classMang) {
            this.lisens = callback;
            this.kol = kol;
            this.classMang = classMang;
            URL2 = nameChar = imgSrc = "";
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
                    doc = Jsoup.connect(classMang.getURL() + classMang.getWhereAll()).userAgent("Mozilla")
                            .timeout(3000)
                            .get();
                }

                Element el = doc.select(classMang.getNameCell()).first();
                for (int i = 0; i < kol_mang; i++)
                    el = el.nextElementSibling();
                Elements el2 = el.select(classMang.getNameURL());

                URL2 = classMang.getURL() + el2.attr("href");
                el2 = el.select(classMang.getImgURL());

                imgSrc = el2.attr("src");
                nameChar = el2.attr("title");
                if (kol_mang == 69 && resultPost != 1) doc = null;
            } catch (IOException e) {
                e.printStackTrace();
                not_net = true;
            }catch (Exception e) {
                //System.out.println("Не грузит страницу либо больше нечего грузить");
                e.printStackTrace();
                stopLoad = true;
            }
            return null;
        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(Void result){
            //добавляем в лист и обновление
            if (!not_net){
                if (kol >= 0 && !URL2.isEmpty() && !nameChar.isEmpty() && !classMang.getURL().isEmpty()) {
                   /* String locNameChar = nameChar;
                    locNameChar = locNameChar.replace(")","").replace(" ","").replace("(",",");*/
                    ClassMainTop classMainTop = new ClassMainTop(URL2, nameChar,imgSrc,classMang.getURL());
                    Log.i("Temple Pase: Kol parse: ", String.valueOf(kol));
                    try {
                        if (list.size() <= kol)
                            list.add(classMainTop);
                        if (resultPost == 0) {
                            classDataBaseListMang.addBasaData(classMainTop, kol);
                        }
                        myAdap.notifyDataSetChanged();

                    }catch (IndexOutOfBoundsException e){
                        //
                        Log.i("Temple Pase: Error: ",e.toString());
                        Log.i("Temple Pase: Size list: ", String.valueOf(list.size()));
                        Log.i("Temple Pase: Kol: ", String.valueOf(kol));
                    }
                    //кричим интерфейсу что мы фсе
                    if (lisens != null) lisens.onEnd();
                }
            }else {
                try{
                    Toast.makeText(fragmentTopMang.this.getContext(), "Что то с инетом", Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e){

                }
            }
            if (lisens != null) lisens.onEnd(1);
        }
    }

    private class InitializationArray  extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            int numberItem = 0;
            while (!classDataBaseListMang.download_the_html(kol)){
                ClassMainTop classTop = classDataBaseListMang.getMainClassTop(kol);
                classTop.setUrlSite(classMang.getURL());
                list.add(classTop);
                kol++;
                numberItem++;
                if (numberItem == 5){
                    publishProgress();
                    numberItem = 0;
                }
                kolSum = kol + 10;
            }
            resultPost = 0;
            if (kol == 0) parssate(kol);
            return null;
        }

        protected void onProgressUpdate(Void... values) {
            myAdap.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }
    }
}
