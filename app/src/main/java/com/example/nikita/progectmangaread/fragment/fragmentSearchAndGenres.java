package com.example.nikita.progectmangaread.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterList;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassForList;
import com.example.nikita.progectmangaread.classPMR.ClassMang;
import com.example.nikita.progectmangaread.classPMR.ClassTransport;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 21.02.2016.
 *
 * Фрагмент для создание post запроса
 *
 * Используется ClassForList где:
 * URL_chapter - имя ключа для post запроса;
 * Name_chapter - имя жанра или т.п.;
 * check - in/" " (на будущее сделать еще исключение (см. поиск readmanga.me))
 *
 * на раздумье: добавить в БД жанры и потом по ним сортировать в топ лист
 *
 */
public class fragmentSearchAndGenres extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {
    View v;
    ArrayList<ClassForList> list;
    public AdapterList myAdap;
    private ListView gr;
    public ClassTransport classMang;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        setRetainInstance(true);
        classMang = new ClassTransport();
        myAdap = new AdapterList(getActivity(), R.layout.layout_for_list_view, list);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("POST", "!!CREATE!!SEARCH");
        v = inflater.inflate(R.layout.search_and_genres_fragment, null);
        gr = (ListView) v.findViewById(R.id.listSearch);
        gr.setAdapter(myAdap);

        Button b = (Button) v.findViewById(R.id.buttonSearch);
        b.setOnClickListener(this);

        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ClassForList classForList1 = list.get(position);
                if (classForList1.getCheck()) classForList1.setCheck(false);
                else classForList1.setCheck(true);
                list.set(position, classForList1);
                myAdap.notifyDataSetChanged();
            }
        });
        EditText editGo = (EditText) v.findViewById(R.id.editText);
        editGo.setOnEditorActionListener(this);
        return v ;
    }

    public void onEvent(ClassMang event){
        this.classMang.setClassMang(event);
        int id = 0;
        if (classMang.getClassMang().getURL().contains("readmanga")) id = R.raw.search_read_manga;
        else if (classMang.getClassMang().getURL().contains("mintmanga")) id = R.raw.search_adultmanga;


        //считываем с ресурсов
        InputStream XmlFileInputStream = getResources().openRawResource(id); // getting XML

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
            ClassForList a = new ClassForList(e.attr("post"),e.text(),-1);
            list.add(a);
        }
        myAdap.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        Log.i("POST", "!!START!!SEARCH");
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.i("POST", "!!PAUSE!!SEARCH");
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
        postRequest();
    }

    private void postRequest(){
        EditText text = (EditText) this.v.findViewById(R.id.editText);
        String request = null;
        try {
            request = "/search?q=" + URLEncoder.encode(text.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (ClassForList a : list){
            String in;
            if (a.getCheck()) in = "in";
            else in="";
            request += "&"+ a.getURL_chapter() + "="+in;
        }
        Log.i("POST", classMang.getClassMang().getURL()+request);
        classMang.setURL_Search(request);
        EventBus.getDefault().post(classMang);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            postRequest();
            //закрытие клавиатуры
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        }
        return false;
    }
}
