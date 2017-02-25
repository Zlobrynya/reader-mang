package com.zlobrynya.project.readermang.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zlobrynya.project.readermang.AdapterPMR.AdapterList;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.classPMR.ClassForList;
import com.zlobrynya.project.readermang.classPMR.ClassMang;
import com.zlobrynya.project.readermang.classPMR.ClassTransport;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Nikita on 04.03.2016.
 */
public class fragmentGenres extends Fragment {
    View v;
    ArrayList<ClassForList> list;
    public AdapterList myAdap;
    public String genres;
    private ListView gr;
    public ClassTransport classTransport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        classTransport = new ClassTransport();
        myAdap = new AdapterList(getActivity(), R.layout.list_view_checkbox, list);
        setRetainInstance(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_view, null);
        gr = (ListView) v.findViewById(R.id.listView);
        gr.setAdapter(myAdap);
        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ClassForList classForList1 = list.get(position);
                genres = classForList1.getURLChapter();
                postRequest();
            }
        });

        return v ;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClassMang event){
        this.classTransport.setClassMang(event);
        int id = 0;
        if (classTransport.getClassMang().getURL().contains("readmanga")) id = R.raw.search_read_manga;
        else if (classTransport.getClassMang().getURL().contains("mintmanga")) id = R.raw.search_mint_manga;
        else if (classTransport.getClassMang().getURL().contains("selfmanga")) id = R.raw.search_self_manga;
        else if (classTransport.getClassMang().getURL().contains("chan")) id = R.raw.search_manga_chan;
        else return;

        list.clear();
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
            if (str.isEmpty()) break;
            ClassForList a = new ClassForList(str,e.text());
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
        String request = null;
        try {
            request = getRequest(genres);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("POST", classTransport.getClassMang().getURL()+request);
        classTransport.getClassMang().setWhere(request);
        classTransport.getClassMang().setPath("");
        classTransport.getClassMang().setPath2("");
        classTransport.setURL_Search("");
        EventBus.getDefault().post(classTransport);
    }

    private String getRequest(String genres) throws UnsupportedEncodingException {
        if (classTransport.getClassMang().getURL().contains("chan")){
            return "/tags/" +  URLEncoder.encode(genres, "UTF-8") + "&n=favdesc?offset=";
        }else {
            return "/list/genre/" + genres;
        }
    }


}
