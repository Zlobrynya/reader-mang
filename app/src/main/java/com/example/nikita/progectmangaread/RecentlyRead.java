package com.example.nikita.progectmangaread;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.example.nikita.progectmangaread.AdapterPMR.AdapterRecentlyRead;
import com.example.nikita.progectmangaread.DataBasePMR.*;
import com.example.nikita.progectmangaread.classPMR.classRecentlyRead;
import com.example.nikita.progectmangaread.classPMR.MainClassTop;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 28.04.2016.
 */
public class RecentlyRead extends BaseActivity{
    private AdapterRecentlyRead adapter;
    private ArrayList<classRecentlyRead> list;
    private classDataBaseListMang classDataBaseListMang;
    private classDataBaseViewedHead classDataBaseViewedHead;
    private ListView listView;
    private SharedPreferences mSettings;
    private static final String APP_PREFERENCES = "settingsListMang";
    private static final String APP_PREFERENCES_URL = "URL";
    private String url;
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        getLayoutInflater().inflate(R.layout.list_heads, frameLayout);
        url = mSettings.getString(APP_PREFERENCES_URL, "");
        pos = -1;
        list = new ArrayList<>();
        //ViewedHead.db
        classDataBaseViewedHead = new classDataBaseViewedHead(this,"ViewedHead.db");
        listView = (ListView) this.findViewById(R.id.listView);
        if (url.contains("readmanga")){
            classDataBaseListMang = new classDataBaseListMang(this,"readmanga.db");
            url = "readmanga";
        }
        if (url.contains("AdultManga")){
            classDataBaseListMang = new classDataBaseListMang(this,"AdultManga.db");
            url = "AdultManga";
        }
        if (!url.isEmpty()) initialization();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                Intent intent = new Intent(RecentlyRead.this,DescriptionMang.class);
                startActivity(intent);
                pos = position;
                Log.v("long clicked", "pos: " + position);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecentlyRead.this,DescriptionMang.class);
                intent.putExtra("read",true);
                startActivity(intent);
                pos = position;
            }
        });
    }

    private void initialization(){
        for (int i =0; i < classDataBaseViewedHead.fetchPlacesCount();i++){
            String nameChapter,nameMang,URLimg,URLchapter_last,URLchapter;
            nameMang = classDataBaseViewedHead.getNameMang(i);
            nameChapter = classDataBaseViewedHead.getDataFromDataBase(nameMang, DataBaseViewedHead.NAME_LAST_CHAPTER);
            if (!nameChapter.contains("null")){
                URLchapter_last = classDataBaseViewedHead.getDataFromDataBase(nameMang,DataBaseViewedHead.LAST_CHAPTER);
                if (URLchapter_last.contains(url)){
                    URLimg = classDataBaseListMang.getDataFromDataBase(nameMang,DatabaseHelper.URL_IMG);
                    if (URLimg.contains("null")){
                        URLimg = classDataBaseViewedHead.getDataFromDataBase(nameMang,DataBaseViewedHead.URL_IMG);
                    }
                    URLchapter = classDataBaseListMang.getDataFromDataBase(nameMang,DatabaseHelper.URL_CHAPTER);
                    if(URLchapter.contains("null")){
                        String s[] = URLchapter_last.split("/");
                        //еще один костыль, тут полюбому придется переделать когда надо будет добавлять др сайты
                        Log.i("string",s[0]+s[1]+s[2]+s[3]);
                        URLchapter = s[0]+"//"+s[2]+"/"+s[3]+"/";
                        Log.i("string",URLchapter);

                    }
                    classRecentlyRead RR = new classRecentlyRead(URLimg,nameMang,nameChapter,URLchapter,URLchapter_last);
                    list.add(RR);
                }
            }
        }
        //для узнавания разрешения экрана
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        adapter = new AdapterRecentlyRead(this,R.layout.list_heads,list,width,height);
        listView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        if (pos > -1){
            MainClassTop classTop = new MainClassTop();
            classTop.setName_characher(list.get(pos).getNameMang());
            //Костыли
            if (list.get(pos).getURLchapter().contains("readmang")){
                classTop.setURL_site("http://readmanga.me");
            }
            if (list.get(pos).getURLchapter().contains("AdultManga")){
                classTop.setURL_site("http://AdultManga.me");
            }
            //
            classTop.setURL_img(list.get(pos).getURL_img());
            classTop.setURL_characher(list.get(pos).getURLchapter());
            EventBus.getDefault().post(classTop);
            pos = -1;
        }
        super.onStop();
    }

}
