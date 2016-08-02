package com.example.nikita.progectmangaread.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterBookmark;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseListMang;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassMainTop;
import com.example.nikita.progectmangaread.classPMR.ClassRecentlyRead;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 09.05.2016.
 */
public class Bookmark extends BaseActivity {
    private AdapterBookmark adapter;
    private ArrayList<ClassRecentlyRead> list;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.list_heads, frameLayout);

        list = new ArrayList<>();
        classDataBaseViewedHead = new ClassDataBaseViewedHead(this);
        listView = (ListView) this.findViewById(R.id.listView);
        getSupportActionBar().setTitle("Bookmark"); // set the top title

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                Intent intent = new Intent(Bookmark.this,DescriptionMang.class);
                ClassMainTop mainTop = getClassMainTop(position);
                if (mainTop != null){
                    intent.putExtra("URL_ch",mainTop.getURL_characher());
                    intent.putExtra("Url_img",mainTop.getURL_img());
                    intent.putExtra("Name_ch",mainTop.getName_characher());
                    intent.putExtra("Url_site", mainTop.getURL_site());
                    startActivity(intent);
                    Log.v("long clicked", "pos: " + position);
                }
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Bookmark.this,DescriptionMang.class);
                ClassMainTop mainTop = getClassMainTop(position);
                if (mainTop != null){
                    intent.putExtra("URL_ch",mainTop.getURL_characher());
                    intent.putExtra("Url_img",mainTop.getURL_img());
                    intent.putExtra("Name_ch",mainTop.getName_characher());
                    intent.putExtra("Url_site",mainTop.getURL_site());
                    intent.putExtra("read", true);
                    startActivity(intent);
                    Log.v("long clicked", "pos: " + position);
                }
            }
        });
        initializationNotebook();
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

    private void initializationNotebook(){
        Cursor cursor = classDataBaseViewedHead.getNotebook();
        Log.i("Cursor: ", String.valueOf(cursor.getCount()));
        cursor.moveToFirst();
        for(int i = 0;i < cursor.getCount();i++){
            String nameMang,nameChapter,URLchapter,URLimg,URLlastChapter;
            nameMang = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.NAME_MANG));
            URLchapter = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.URL_CHAPTER));
            URLlastChapter = cursor.getString(cursor.getColumnIndex(ClassDataBaseViewedHead.LAST_CHAPTER));
            URLimg = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.URL_IMG));
            nameChapter = cursor.getString(cursor.getColumnIndex(ClassDataBaseViewedHead.NAME_LAST_CHAPTER));
            list.add(new ClassRecentlyRead(URLimg,nameMang,nameChapter,URLchapter,URLlastChapter));
            cursor.moveToNext();
        }
        cursor.close();

        //размер экрана
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        adapter = new AdapterBookmark(this,R.layout.list_heads,list,width,height);
        listView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void imageButtonDelete(View view) {
        int poss = (int) view.getTag();
        Toast.makeText(this, "Delete: " + list.get(poss).getNameMang(),
                Toast.LENGTH_SHORT).show();
        classDataBaseViewedHead.setData(list.get(poss).getNameMang(), "0", ClassDataBaseViewedHead.NOTEBOOK);
        list.remove(poss);
        adapter.notifyDataSetChanged();
    }
}