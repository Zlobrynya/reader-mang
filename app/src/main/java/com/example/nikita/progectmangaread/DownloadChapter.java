package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterList;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseDownloadMang;
import com.example.nikita.progectmangaread.classPMR.ClassForList;
import com.example.nikita.progectmangaread.classPMR.ClassTransportForList;
import com.example.nikita.progectmangaread.service.ServiceDownChapter;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class DownloadChapter extends AppCompatActivity {
    ArrayList<ClassForList> list;
    private AdapterList myAdap;
    private ListView listView;
    private String urlMang, urlSite, name;

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
        urlMang = intent.getStringExtra("mang");
        urlSite = intent.getStringExtra("site");
        name = intent.getStringExtra("Name");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ClassForList classForList1 = list.get(position);
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
                String nameChapter = "";
                for (ClassForList classForList: list){
                    if (classForList.getCheck()) {
                        chapter += classForList.getURL_chapter() + ",";
                        nameChapter += classForList.getName_chapter() + ",";
                    }
                }
                ClassDataBaseDownloadMang classDataBaseDownloadMang = new ClassDataBaseDownloadMang(DownloadChapter.this);
                classDataBaseDownloadMang.setData(name,chapter,ClassDataBaseDownloadMang.NAME_DIR);
                classDataBaseDownloadMang.setData(name, nameChapter, ClassDataBaseDownloadMang.NAME_CHAPTER);

               // Log.i("DownloadChapter", chapter.substring(0,chapter.length()-2));
                startService(new Intent(DownloadChapter.this, ServiceDownChapter.class).putExtra("URL_Mang", urlMang)
                        .putExtra("name_site", urlSite)
                        .putExtra("chapter", chapter));

                Toast.makeText(DownloadChapter.this, "Mang download.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onDestroy(){
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEvent(ClassTransportForList event){
        if (!event.getName().isEmpty()){
            list.clear();
            ArrayList<ClassForList> arrayList = event.getClassForList();
            for (ClassForList b: arrayList){
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
