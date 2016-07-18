package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterList;
import com.example.nikita.progectmangaread.classPMR.classForList;
import com.example.nikita.progectmangaread.classPMR.classTransportForList;
import com.example.nikita.progectmangaread.servise.DownChapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;

public class DownloadChapter extends AppCompatActivity {
    ArrayList<classForList> list;
    private AdapterList myAdap;
    private ListView listView;
    private String url_mang,url_site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_chapter);
        list = new ArrayList<>();
        myAdap = new AdapterList(this, R.layout.layout_for_list_view, list);
        listView = (ListView) findViewById(R.id.listChapter);
        listView.setAdapter(myAdap);
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        url_mang = intent.getStringExtra("mang");
        url_site = intent.getStringExtra("site");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                classForList classForList1 = list.get(position);
                classForList1.setCheck(!classForList1.getCheck());
                //Log.i("DownloadChapter", classForList1.getURL_chapter());
                classForList1.setNumberChapter(position);
                list.set(position, classForList1);
                myAdap.notifyDataSetChanged();
            }
        });

        Button downloadButton = (Button) findViewById(R.id.download_btn);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String chapter = "";
                for (classForList classForList: list){
                    if (classForList.getCheck())
                        chapter += classForList.getURL_chapter() + ",";
                }
               // Log.i("DownloadChapter", chapter.substring(0,chapter.length()-2));
                startService(new Intent(DownloadChapter.this, DownChapter.class).putExtra("URL_Mang", url_mang)
                        .putExtra("name_site", url_site)
                        .putExtra("chapter", chapter));
            }
        });
    }

    public void onEvent(classTransportForList event){
        if (!event.getName().isEmpty()){
            list.clear();
            ArrayList<classForList> arrayList = event.getClassForList();
            for (classForList b: arrayList){
                if (b.getCheck())
                    b.setCheck(false);
                String[] strings = b.getURL_chapter().split("/");
                b.setURL_chapter("/"+strings[2]+"/"+strings[3].replace("?mature=1",""));
                list.add(b);
            }
            myAdap.notifyDataSetChanged();
        }
    }
}
