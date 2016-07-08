package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterNotebook;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseListMang;
import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseViewedHead;
import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classRecentlyRead;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 09.05.2016.
 */
public class Bookmark extends BaseActivity {
    private AdapterNotebook adapter;
    private ArrayList<classRecentlyRead> list;
    private com.example.nikita.progectmangaread.DataBasePMR.classDataBaseViewedHead classDataBaseViewedHead;
    private ListView listView;
    private int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.list_heads, frameLayout);

        list = new ArrayList<>();
        classDataBaseViewedHead = new classDataBaseViewedHead(this);

        pos = - 1;

        listView = (ListView) this.findViewById(R.id.listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                Intent intent = new Intent(Bookmark.this, DescriptionMang.class);
                startActivity(intent);
                pos = position;
                Log.v("long clicked", "pos: " + position);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Bookmark.this, DescriptionMang.class);
                intent.putExtra("read", true);
                startActivity(intent);
                pos = position;
            }
        });

        initializationNotebook();

    }

    private void initializationNotebook(){
        Cursor cursor = classDataBaseViewedHead.getNotebook();
        Log.i("Cursor: ", String.valueOf(cursor.getCount()));
        cursor.moveToFirst();
        for(int i = 0;i < cursor.getCount();i++){
            String nameMang,nameChapter,URLchapter,URLimg,URLlastChapter;
            nameMang = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.NAME_MANG));
            URLchapter = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.URL_CHAPTER));
            URLlastChapter = cursor.getString(cursor.getColumnIndex(classDataBaseViewedHead.LAST_CHAPTER));
            URLimg = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.URL_IMG));
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
        adapter = new AdapterNotebook(this,R.layout.list_heads,list,width,height);
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

    public void DeleteNotebook(View view) {
        int poss = (int) view.getTag();
        Toast.makeText(this, "Delete: " + list.get(poss).getNameMang(),
                Toast.LENGTH_SHORT).show();
        classDataBaseViewedHead.setData(list.get(poss).getNameMang(), "0", com.example.nikita.progectmangaread.DataBasePMR.classDataBaseViewedHead.NOTEBOOK);
        list.remove(poss);
        adapter.notifyDataSetChanged();
    }
}