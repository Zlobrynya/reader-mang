package com.zlobrynya.project.ereadermanga.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;


import com.zlobrynya.project.ereadermanga.AdapterPMR.AdapterRecentlyRead;
import com.zlobrynya.project.ereadermanga.DataBasePMR.*;
import com.zlobrynya.project.ereadermanga.R;
import com.zlobrynya.project.ereadermanga.classPMR.ClassRecentlyRead;
import com.zlobrynya.project.ereadermanga.classPMR.ClassMainTop;

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
    private TextView textView;

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
        textView= (TextView) findViewById(R.id.text_sticky_list);

        url = mSettings.getString(APP_PREFERENCES_URL, "");
        //ViewedHead.db
        if (url.contains("readmanga")){
            url = "readmanga";
        }
        if (url.contains("mintmanga")){
            url = "mintmanga";
        }
        if (url.contains("selfmanga")){
            url = "selfmanga";
        }
        if (!url.isEmpty()) initializationRR();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                Intent intent = new Intent(RecentlyRead.this,DescriptionMang.class);
                ClassMainTop mainTop = getClassMainTop(position);
                if (mainTop != null){
                    intent.putExtra("URL_ch",mainTop.getURLCharacher());
                    intent.putExtra("Url_img",mainTop.getUrlImg());
                    intent.putExtra("Name_ch",mainTop.getNameCharacher());
                    intent.putExtra("Url_site",mainTop.getUrlSite());
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
                    intent.putExtra("URL_ch",mainTop.getURLCharacher());
                    intent.putExtra("Url_img",mainTop.getUrlImg());
                    intent.putExtra("Name_ch",mainTop.getNameCharacher());
                    intent.putExtra("Url_site",mainTop.getUrlSite());
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
            classTop.setNameCharacher(list.get(pos).getNameMang());
            //Костыли
            if (list.get(pos).getURLchapter().contains("readmang")){
                classTop.setUrlSite("http://readmanga.me");
            }
            if (list.get(pos).getURLchapter().contains("mintmanga")){
                classTop.setUrlSite("http://mintmanga.com");
            }
            if (list.get(pos).getURLchapter().contains("selfmang")){
                classTop.setUrlSite("http://selfmang.ru");
            }
            //
            classTop.setUrlImg(list.get(pos).getURL_img());
            classTop.setUrlCharacher(list.get(pos).getURLchapter());
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
        if (list.isEmpty()){
            textView.setText(R.string.not_recentle_read);
            textView.setVisibility(View.VISIBLE);
        }
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
