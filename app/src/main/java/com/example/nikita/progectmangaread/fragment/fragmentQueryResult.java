package com.example.nikita.progectmangaread.fragment;

import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classTransport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 23.02.2016.
 */
public class fragmentQueryResult extends Fragment{

    public int lastItem,firstItem,height,width,page;
    public int kol,kolSum,kolSum_previous,kol_previous,kolImage;
    public Document doc;
    public LinkedList<MainClassTop> list;
    public AdapterMainScreen myAdap;
    public GridView gr;
    public boolean mIsScrollingUp;
    public classTransport classTransport;

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
        myAdap = new AdapterMainScreen(getActivity(), R.layout.layout_from_graund_view,list,width,height);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        kol = 0;
        mIsScrollingUp = false;
        kolSum = 28;
        kolSum_previous = 0;
        kol_previous = kolImage = 0;
        View v = inflater.inflate(R.layout.fragment, null);

        gr = (GridView) v.findViewById(R.id.gread_id);
        gr.setAdapter(myAdap);

        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainClassTop Class = list.get(position);
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
                        //Берем последнюю видемою
                        kol = lastVisibleItem;
                        //считаем до куда будем скачивать (16 видем и 12 за кадром)
                        kolSum_previous = lastVisibleItem - 22;
                        if (kol_previous < 0) kolSum_previous = 0;
                        //переползаем через класс если там есть картинка
                        for (; kol > kolSum_previous && kol > 0; kol--) {
                            Log.i("kol:", "Up" + kol);
                            MainClassTop a = list.get(kol);
                            if (a.getImg_characher() == null) break;
                        }
                        Log.i("Scroll 1:", "Up");
                    }
                    if (firstItem < firstVisibleItem) {
                        mIsScrollingUp = false; //DOWN \/
                        kol = firstVisibleItem;
                        kolSum = firstVisibleItem + 22;
                        for (; kol < kolSum && kol < list.size(); kol++) {
                            Log.i("kol:", "Down" + kol);
                            MainClassTop a = list.get(kol);
                            if (a.getImg_characher() == null) break;
                        }
                        Log.i("Scroll 1:", "Down, kolSum " + kolSum + " kol: " + kol);
                    }
                }
                firstItem = firstVisibleItem;
                lastItem = lastVisibleItem;
                parssate(kol);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Log.i("Scroll 2", String.valueOf(helpVasr));
            }
        });

        System.out.println("!! " + width + " " + height);
        return v ;
    }

    //создается клас с описанием интерфейсв
    AsyncTaskLisen addImg = new AsyncTaskLisen() {
        @Override
        public void onEnd() {
            //if UP scroll
            if (mIsScrollingUp){
                if (kol > kolSum_previous){
                    kol--;
                    parssate(kol);
                }
            }else {
                if (kol < kolSum) {
                    kol++;
                    parssate(kol);
                }
            }
        }
    };

    public void onEvent(classTransport event){
        classTransport = event;
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
    public static fragmentQueryResult newInstance(int page) {
        fragmentQueryResult fragment = new fragmentQueryResult();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    //метод парсим
    public void parssate(int kol){
        //парсим сайт
        Pars past = new Pars(addImg,kol);
        past.execute();
    }


    public class Pars extends AsyncTask<Void,Void,Void> {
        private String name_char,URL2;
        private Bitmap img;
        private AsyncTaskLisen lisens;
        private String imgSrc;
        private int kol;

        //конструктор потока
        protected Pars(AsyncTaskLisen callback, int kol) {
            this.lisens = callback;
            this.kol = kol;
        }

        @Override
        protected  void  onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Document doc;
            try {
                if (doc == null) {
                    doc = Jsoup.connect(classTransport.getURL_Search()).get();
                }

                Element el = doc.select(classTransport.getClassMang().getNameCell()).first();
                for (int i = 0; i < 200; i++)
                    el = el.nextElementSibling();
                Elements el2 = el.select(classTransport.getClassMang().getNameUML());
                URL2 = classTransport.getClassMang().getUML() + el2.attr("href");
                el2 = el.select(classTransport.getClassMang().getImgUML());

                imgSrc = el2.attr("src");
                name_char = el2.attr("title");

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
            if (img != null && kol >= 0) {
                MainClassTop a = new MainClassTop(img, URL2,name_char);
                a.editClass(width, height);
                Log.i("Kol parse: ", String.valueOf(kol));
                if (list.size() <= kol) list.add(kol,a);
                else list.set(kol, a);

                if (mIsScrollingUp){
                    int quantity = kol + 41;
                    if (quantity <= list.size()){
                        MainClassTop a1 = list.get(quantity);
                        a1.deleteImg();
                        list.set(quantity, a1);
                        Log.i("Delete: ", "quantity: " + quantity + " KOL: " + kol + " name: " + a1.getName_characher() + " mIsScrollingUp " + mIsScrollingUp);
                    }
                }else {
                    int quantity = kol - 41;
                    if (quantity >= 0){
                        MainClassTop a1 = list.get(quantity);
                        a1.deleteImg();
                        list.set(quantity, a1);
                        Log.i("Delete: ", "quantity: " + quantity + " KOL: " + kol + " name: " + a1.getName_characher() + " mIsScrollingUp " + mIsScrollingUp);
                    }
                }

                myAdap.notifyDataSetChanged();
                //кричим интерфейсу что мы фсе
                if (lisens != null) lisens.onEnd();
            }
        }
    }
}
