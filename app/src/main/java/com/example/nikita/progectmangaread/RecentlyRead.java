package com.example.nikita.progectmangaread;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
        pos = -1;
        list = new ArrayList<>();
        classDataBaseViewedHead = new classDataBaseViewedHead(this);
        listView = (ListView) this.findViewById(R.id.listView);

        url = mSettings.getString(APP_PREFERENCES_URL, "");
        //ViewedHead.db
        if (url.contains("readmanga")){
            url = "readmanga";
        }
        if (url.contains("mintmanga")){
            url = "mintmanga";
        }
        if (!url.isEmpty()) initializationRR();

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

    private void initializationRR(){
        Cursor cursor = classDataBaseViewedHead.getViewedChapter(url);
        Log.i("Cursor: ", String.valueOf(cursor.getCount()));
        cursor.moveToFirst();
        for(int i = 0;i < cursor.getCount();i++){
            String nameMang,nameChapter,URLchapter,URLimg,URLlastChapter;
            nameMang = cursor.getString(cursor.getColumnIndex(classDataBaseListMang.NAME_MANG));
            URLchapter = cursor.getString(cursor.getColumnIndex(classDataBaseListMang.URL_CHAPTER));
            URLlastChapter = cursor.getString(cursor.getColumnIndex(classDataBaseViewedHead.LAST_CHAPTER));
            URLimg = cursor.getString(cursor.getColumnIndex(classDataBaseListMang.URL_IMG));
            nameChapter = cursor.getString(cursor.getColumnIndex(classDataBaseViewedHead.NAME_LAST_CHAPTER));
            list.add(new classRecentlyRead(URLimg,nameMang,nameChapter,URLchapter,URLlastChapter));
            cursor.moveToNext();
        }
        cursor.close();

        //размер экрана
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
            if (list.get(pos).getURLchapter().contains("mintmanga")){
                classTop.setURL_site("http://mintmanga.com");
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
