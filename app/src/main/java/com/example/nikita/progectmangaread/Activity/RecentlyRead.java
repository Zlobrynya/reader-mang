package com.example.nikita.progectmangaread.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;


import com.example.nikita.progectmangaread.AdapterPMR.AdapterRecentlyRead;
import com.example.nikita.progectmangaread.DataBasePMR.*;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassRecentlyRead;
import com.example.nikita.progectmangaread.classPMR.ClassMainTop;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Nikita on 28.04.2016.
 */
public class RecentlyRead extends BaseActivity{
    private AdapterRecentlyRead adapter;
    private ArrayList<ClassRecentlyRead> list;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private static final String APP_PREFERENCES = "settingsListMang";
    private static final String APP_PREFERENCES_URL = "URL";
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        getLayoutInflater().inflate(R.layout.sticky_list, frameLayout);
        list = new ArrayList<>();
        classDataBaseViewedHead = new ClassDataBaseViewedHead(this);
        StickyListHeadersListView listView = (StickyListHeadersListView) this.findViewById(R.id.listRecentlyRead);
        adapter = new AdapterRecentlyRead(this,R.layout.list_view,list);
        listView.setAdapter(adapter);
        getSupportActionBar().setTitle("Recently Read"); // set the top title

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
                ClassMainTop mainTop = getClassMainTop(position);
                if (mainTop != null){
                    intent.putExtra("URL_ch",mainTop.getURL_characher());
                    intent.putExtra("Url_img",mainTop.getURL_img());
                    intent.putExtra("Name_ch",mainTop.getName_characher());
                    intent.putExtra("Url_site",mainTop.getURL_site());
                    startActivity(intent);
                    Log.v("long clicked", "pos: " + position);
                }
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RecentlyRead.this,DescriptionMang.class);
                ClassMainTop mainTop = getClassMainTop(position);
                if (mainTop != null){
                    intent.putExtra("URL_ch",mainTop.getURL_characher());
                    intent.putExtra("Url_img",mainTop.getURL_img());
                    intent.putExtra("Name_ch",mainTop.getName_characher());
                    intent.putExtra("Url_site",mainTop.getURL_site());
                    intent.putExtra("read",true);
                    startActivity(intent);
                    Log.v("long clicked", "pos: " + position);
                }
            }
        });
    }

    private ClassMainTop getClassMainTop(int pos){
        if (pos > -1){
            ClassMainTop classTop = new ClassMainTop();
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
            return classTop;
        }
        return null;
    }

    private void initializationRR(){
        Cursor cursor = classDataBaseViewedHead.getViewedChapter(url);
        Log.i("Cursor: ", String.valueOf(cursor.getCount()));
        cursor.moveToFirst();
        for(int i = 0;i < cursor.getCount();i++){
            String nameMang,nameChapter,URLchapter,URLimg,URLlastChapter,date;
            nameMang = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.NAME_MANG));
            URLchapter = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.URL_CHAPTER));
            URLlastChapter = cursor.getString(cursor.getColumnIndex(classDataBaseViewedHead.LAST_CHAPTER));
            URLimg = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.URL_IMG));
            nameChapter = cursor.getString(cursor.getColumnIndex(classDataBaseViewedHead.NAME_LAST_CHAPTER));
            date = cursor.getString(cursor.getColumnIndex(classDataBaseViewedHead.DATA));
            if (date != null && !nameChapter.contains("null"))
                list.add(new ClassRecentlyRead(URLimg,nameMang,nameChapter,URLchapter,URLlastChapter,date));
            cursor.moveToNext();
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        list.clear();
        initializationRR();
        super.onResume();
    }


    @Override
    public void onStop() {
        super.onStop();
    }
}
