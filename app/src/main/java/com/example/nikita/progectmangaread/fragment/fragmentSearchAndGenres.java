package com.example.nikita.progectmangaread.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterList;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.classForList;
import com.example.nikita.progectmangaread.classPMR.classMang;
import com.example.nikita.progectmangaread.classPMR.classTransport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 21.02.2016.
 *
 * Фрагмент для создание post запроса
 *
 * Используется classForList где:
 * URL_chapter - имя ключа для post запроса;
 * Name_chapter - имя жанра или т.п.;
 * check - in/" " (на будущее сделать еще исключение (см. поиск readmanga.me))
 *
 * Сделать файлы для разных сайтом
 * Сделать разделение на поиск (показывается только первые 200) и на жанры (тут как обычный топ,
 *         на раздумье: добавить в БД жанры и потом по ним сортировать в топ лист)
 * Сделать что бы при нажатие в клавиатуре "enter" "нажималась" кнопка поиска
 *
 */
public class fragmentSearchAndGenres extends Fragment implements View.OnClickListener {
    View v;
    ArrayList<classForList> list;
    public AdapterList myAdap;
    private ListView gr;
    public classTransport classMang;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        //считываем с ресурсов
        InputStream XmlFileInputStream = getResources().openRawResource(R.raw.search_read_manga); // getting XML

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //открываем файл и считываем от туда инфу
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = XmlFileInputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            XmlFileInputStream.close();
        } catch (IOException e) {

        }
        //Парсим то что скачали с файла
        Document doc = Jsoup.parse(outputStream.toString(), "", Parser.xmlParser());
        for (Element e : doc.select("genres")) {
            classForList a = new classForList(e.text(),e.attr("name"));
            list.add(a);
        }

        classMang = new classTransport();
        myAdap = new AdapterList(getActivity(), R.layout.layout_for_list_view, list);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.search_and_genres_fragment, null);
        gr = (ListView) v.findViewById(R.id.listSearch);
        gr.setAdapter(myAdap);

        Button b = (Button) v.findViewById(R.id.buttonSearch);
        b.setOnClickListener(this);

        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                classForList classForList1 = list.get(position);
                if (classForList1.getCheck()) classForList1.setCheck(false);
                else classForList1.setCheck(true);
                list.set(position, classForList1);
                myAdap.notifyDataSetChanged();
            }
        });

        return v ;
    }

    public void onEvent(classMang event){
        this.classMang.setClassMang(event);
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i("POST", "!!STOP!!");
        if (classMang.getClassMang() != null)
            EventBus.getDefault().post(classMang);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.i("POST", "!!PAUSE!!");
        super.onPause();
    }
    //фабричный метод для ViewPage
    public static fragmentSearchAndGenres newInstance(int page) {
        fragmentSearchAndGenres fragment = new fragmentSearchAndGenres();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        EditText text = (EditText) this.v.findViewById(R.id.editText);
        String request = null;
        try {
            request = classMang.getClassMang().getUML() + "/search?q=" + URLEncoder.encode(text.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (classForList a : list){
            String in;
            if (a.getCheck()) in = "in";
                else in="";
            request += "&"+ a.getURL_chapter() + "="+in;
        }
        Log.i("POST", request);
        classMang.setURL_Search(request);
        EventBus.getDefault().post(classMang);
    }
}
