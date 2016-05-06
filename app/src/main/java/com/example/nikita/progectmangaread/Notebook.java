package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterNotebook;
import com.example.nikita.progectmangaread.DataBasePMR.DataBaseViewedHead;
import com.example.nikita.progectmangaread.DataBasePMR.DatabaseHelper;
import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseListMang;
import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseViewedHead;
import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classRecentlyRead;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class Notebook extends BaseActivity {
    private AdapterNotebook adapter;
    private ArrayList<classRecentlyRead> list;
    private classDataBaseViewedHead classDataBaseViewedHead;
    private ListView listView;
    private int pos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.list_heads, frameLayout);

        list = new ArrayList<>();
        classDataBaseViewedHead = new classDataBaseViewedHead(this,"ViewedHead.db");

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
                Intent intent = new Intent(Notebook.this,DescriptionMang.class);
                startActivity(intent);
                pos = position;
                Log.v("long clicked", "pos: " + position);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Notebook.this,DescriptionMang.class);
                intent.putExtra("read",true);
                startActivity(intent);
                pos = position;
            }
        });


        initialization();
    }

    private void initialization() {
        for (int i =0; i < classDataBaseViewedHead.fetchPlacesCount();i++){
            String nameChapter,nameMang,URLimg,URLchapter_last,URLchapter;
            nameMang = classDataBaseViewedHead.getNameMang(i);
            if (Integer.parseInt(classDataBaseViewedHead.getDataFromDataBase(nameMang,DataBaseViewedHead.NOTEBOOK)) == 1){
                nameChapter = classDataBaseViewedHead.getDataFromDataBase(nameMang, DataBaseViewedHead.NAME_LAST_CHAPTER);
                if (!nameChapter.contains("null")){
                    URLchapter_last = classDataBaseViewedHead.getDataFromDataBase(nameMang, DataBaseViewedHead.LAST_CHAPTER);
                    URLimg = classDataBaseViewedHead.getDataFromDataBase(nameMang,DataBaseViewedHead.URL_IMG);
                    String s[] = URLchapter_last.split("/");
                    //еще один костыль, тут полюбому придется переделать когда надо будет добавлять др сайты
                    Log.i("string", s[0] + s[1] + s[2] + s[3]);
                            URLchapter = s[0]+"//"+s[2]+"/"+s[3]+"/";
                            Log.i("string",URLchapter);
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
