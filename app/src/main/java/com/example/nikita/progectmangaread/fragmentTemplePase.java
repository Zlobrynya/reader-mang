package com.example.nikita.progectmangaread;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
    public String URL,nameCell,nameURL,nameIMG,where;
    public int kol,kolSum,totalSum;
    public Document doc;
    public ArrayList<MainClassTop> list;
    public AdapterMainScreen myAdap;
    public GridView gr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        kol = 0;
        kolSum = 20;
        list = new ArrayList<>();
        View v = inflater.inflate(R.layout.fragment, null);
        gr = (GridView)v.findViewById(R.id.gread_id);
        myAdap = new AdapterMainScreen(getActivity(),R.layout.layout_from_graund_view,list);
        gr.setAdapter(myAdap);
        return v ;
    }


    public void editTemplePase(classMang event){
        URL = event.getUML();
        where = event.getWhere();
        nameURL = event.getNameUML();
        nameCell = event.getNameCell();
        nameIMG = event.getImgUML();
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
    }

    //обнова экрана
    //Как нить завернуть отдельно?
    //ПЕРЕСМОТРЕТЬ ПАРСИНГ!
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
                System.out.println(view.getFirstVisiblePosition() + 18);
                // if (scrollState == 1)
                if (view.getFirstVisiblePosition() + 18 == totalSum) {
                    kolSum += 6;
                    kol++;
                    parssate();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                totalSum = totalItemCount;
                if (kol < kolSum) {
                    kol++;
                    parssate();
                }
            }
        });
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
                if (doc == null) doc = Jsoup.connect(URL + where).get();
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
            }catch (Exception e) {
                System.out.println("Не грузит страницу либо больше нечего грузить");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //добавляем в лист и обновление
            MainClassTop a = new MainClassTop(img,URL2);
            myAdap.setItem(a,kol);
            myAdap.notifyDataSetChanged();
            //кричим интерфейсу что мы фсе
            if (lisens != null) lisens.onEnd();
        }
    }


}
