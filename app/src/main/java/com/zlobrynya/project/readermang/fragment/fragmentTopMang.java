package com.zlobrynya.project.readermang.fragment;

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

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.Activity.DescriptionMang;
import com.zlobrynya.project.readermang.AdapterPMR.AdapterMainScreen;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.ParsSite.tools.HelperParsTopList;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.classPMR.ClassMang;
import com.zlobrynya.project.readermang.classPMR.ClassTransport;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseListMang;
import com.zlobrynya.project.readermang.Activity.TopManga;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    private int firstItem;
    private int kol,kolSum;
    private LinkedList<ClassMainTop> list;
    private AdapterMainScreen myAdap;
    private ClassDataBaseListMang classDataBaseListMang;
    private GridView gr;
    private boolean visibleButton;
    private int resultPost = 0;
    private ClassMainTop mainTop;
    private final boolean DEBUG = false;
    private HelperParsTopList parsTopList;


    public void clearData() {
        classMang = null;
        mainTop = null;
        if (list != null){
            list.clear();
            myAdap.notifyDataSetChanged();
        }
        if (parsTopList != null)
            parsTopList.clearData();
        kol = firstItem = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new LinkedList<>();
        visibleButton = true;
        kol = firstItem = kolSum = 0;
        parsTopList = new HelperParsTopList(getContext(),list);
        parsTopList.setCallback(addImg);
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
        parsTopList.setAdapter(myAdap);

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
                    if (kolSum > kol && !parsTopList.isStopLoad())
                        parsTopList.startPars(kol);
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

        if (resultPost > 0){
            parsTopList.clearData();
            parsTopList.setClassMang(classMang);
            parsTopList.setResultPost(resultPost);
            parsTopList.startPars(kol);
        }

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
        if (DEBUG)
            Log.i("temp Pase","result");
        //Log.i(PROBLEM, "onActivityCreated");

    }

    //создается клас с описанием интерфейсв
    AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            if (kol < kolSum && !parsTopList.isStopLoad()) {
                if (DEBUG)
                    Log.i("Lisener", String.valueOf(kol)+" "+kolSum);
                kol++;
                parsTopList.startPars(kol);
            }
        }

        @Override
        public void onEnd(int number) {
            if (list.isEmpty() ){
                try {
                    TextView textView = (TextView) getActivity().findViewById(R.id.text_top_mang_info);
                    textView.setVisibility(View.VISIBLE);
                }catch (NullPointerException e){
                    Crashlytics.logException(e);
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClassMang event){
        if (event.getURL().isEmpty())
            return;
        if (classMang != null){
            if (!event.getURL().contains(classMang.getURL())){
                list.clear();
                kol = 0;
                parsTopList.clearData();
            }
        }
        if (event == null)
            return;
        classMang = event;
        parsTopList.setClassMang(classMang);
        //создание базы данных
        String nameTable = classMang.getURL().replace(".me", " ");
        nameTable = nameTable.replace("http://"," ");
        nameTable = nameTable.replace(".ru", " ");
        classDataBaseListMang = new ClassDataBaseListMang(getContext(),nameTable);
        parsTopList.setClassDataBaseListMang(classDataBaseListMang);
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
        if (parsTopList != null){
            parsTopList.setResultPost(resultPost);
            parsTopList.setClassMang(classMang);
            parsTopList.startPars(kol);
        }
       // parsTopList.parssate(kol);
     //   Log.i(PROBLEM, "add(ClassTransport ev)");
    }

    private void startPars(){

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

    private class InitializationArray  extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            int numberItem = 0;
            while (!classDataBaseListMang.download_the_html(kol)){
                ClassMainTop classTop = classDataBaseListMang.getMainClassTop(kol);
                if (classTop != null){
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
            }
            if (kol == 0)
                parsTopList.startPars(kol);
            return null;
        }

        protected void onProgressUpdate(Void... values) {
            myAdap.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }
    }
}
