package com.example.nikita.progectmangaread.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterList;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseDownloadMang;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.cacheImage.CacheFile;
import com.example.nikita.progectmangaread.classPMR.ClassForList;
import com.example.nikita.progectmangaread.classPMR.ClassTransportForList;
import com.example.nikita.progectmangaread.service.ServiceDownChapter;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class DownloadChapter extends AppCompatActivity {
    protected ArrayList<ClassForList> list;
    protected AdapterList myAdap;
    private ListView listView;
    private String urlMang, urlSite, name,urlImageMang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_chapter);
        list = new ArrayList<>();
        myAdap = new AdapterList(this, R.layout.list_view_checkbox, list);
        listView = (ListView) findViewById(R.id.listChapter);
        listView.setAdapter(myAdap);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        urlMang = intent.getStringExtra("mang");
        urlSite = intent.getStringExtra("site");
        urlImageMang = intent.getStringExtra("image");
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
                String startStr = urlMang.replace(urlSite, "").replace("/", "");
                String nameDir = "";
                for (ClassForList classForList : list) {
                    if (classForList.getCheck() && !classForList.getCheckDownload()) {
                        int numbr = list.indexOf(classForList);
                        String s[] = classForList.getURL_chapter().split("/");
                        nameDir += startStr + "/" + s[2] + "_" + s[3] + ",";
                        chapter += classForList.getURL_chapter() + ",";
                        nameChapter += classForList.getName_chapter() + ",";
                        classForList.setCheckDownload(true);
                        list.set(numbr, classForList);
                    }
                }
                if (!nameDir.isEmpty()){
                    ClassDataBaseDownloadMang classDataBaseDownloadMang = new ClassDataBaseDownloadMang(DownloadChapter.this);
                    String nameDirFromBD = classDataBaseDownloadMang.getDataFromDataBase(name, ClassDataBaseDownloadMang.NAME_DIR) + nameDir;
                    String nameChapterFromBD = classDataBaseDownloadMang.getDataFromDataBase(name, ClassDataBaseDownloadMang.NAME_CHAPTER) + nameChapter;
                    classDataBaseDownloadMang.setData(name, nameDirFromBD, ClassDataBaseDownloadMang.NAME_DIR);
                    classDataBaseDownloadMang.setData(name, nameChapterFromBD, ClassDataBaseDownloadMang.NAME_CHAPTER);
                    classDataBaseDownloadMang.setData(name, startStr + "/imgGlav", ClassDataBaseDownloadMang.NAME_IMG);
                    classDataBaseDownloadMang.closeDataBase();

                    CacheFile fileGlavImageMang = new CacheFile(getCacheDir(), startStr);
                    fileGlavImageMang.checkFile(urlImageMang, "imgGlav");

                    // Log.i("DownloadChapter", chapter.substring(0,chapter.length()-2));
                    startService(new Intent(DownloadChapter.this, ServiceDownChapter.class).putExtra("URL_Mang", urlMang)
                            .putExtra("url_site", urlSite)
                            .putExtra("chapter", chapter)
                            .putExtra("name_dir", nameDir));

                    Toast.makeText(DownloadChapter.this, "Mang download.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }


    public void onEvent(ClassTransportForList event){
        if (!event.getName().isEmpty()){
            list.clear();
            ArrayList<ClassForList> arrayList = event.getClassForList();
            for (ClassForList b: arrayList){
                if (b.getCheck())
                    b.setCheck(false);
                b.setURL_chapter(b.getURL_chapter().replace("?mature=1",""));
                list.add(b);
            }
            myAdap.notifyDataSetChanged();
        }
    }
}
