package com.example.nikita.progectmangaread.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 04.03.2016.
 */
public class fragmentGenres extends Fragment {
    View v;
    ArrayList<classForList> list;
    public AdapterList myAdap;
    public String genres;
    private ListView gr;
    public classTransport classTransport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        classTransport = new classTransport();
        myAdap = new AdapterList(getActivity(), R.layout.layout_for_list_view, list);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_heads, null);
        gr = (ListView) v.findViewById(R.id.listView);
        gr.setAdapter(myAdap);
        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                classForList classForList1 = list.get(position);
                genres = classForList1.getURL_chapter();
                postRequest();
            }
        });

        return v ;
    }

    public void onEvent(com.example.nikita.progectmangaread.classPMR.classMang event){
        this.classTransport.setClassMang(event);
        int id = 0;
        if (classTransport.getClassMang().getUML() == "http://readmanga.me") id = R.raw.search_read_manga;
        else if (classTransport.getClassMang().getUML() == "http://AdultManga.ru") id = R.raw.search_adultmanga;

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
        } catch (IOException ignored) {

        }
        //Парсим то что скачали с файла
        Document doc = Jsoup.parse(outputStream.toString(), "", Parser.xmlParser());
        for (Element e : doc.select("genres")) {
            String str = e.attr("list");
            if (str == "") break;
            classForList a = new classForList(str,e.text());
            list.add(a);
        }
        myAdap.notifyDataSetChanged();
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

    @Override
    public void onPause() {
        super.onPause();
    }

    //фабричный метод для ViewPage
    public static fragmentGenres newInstance(int page) {
        fragmentGenres fragment = new fragmentGenres();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    private void postRequest(){
        String request = "/list/genre/" + genres;
        Log.i("POST", classTransport.getClassMang().getUML()+request);
        classTransport.setURL_Search(request);
        EventBus.getDefault().post(classTransport);
    }
}
