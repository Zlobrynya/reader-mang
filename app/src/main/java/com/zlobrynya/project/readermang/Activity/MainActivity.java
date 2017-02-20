package com.zlobrynya.project.readermang.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zlobrynya.project.readermang.AdapterPMR.AdapterListSite;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.classPMR.ClassMang;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends BaseActivity {
    private Intent newInten;
    private ClassMang clManga;
    private ArrayList<String> strings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);

        SharedPreferences mSettings = getSharedPreferences(TopManga.APP_SETTINGS, MODE_PRIVATE);

        strings = new ArrayList<>();
        if (mSettings.getBoolean(TopManga.APP_SETTINGS_SITE_READMANG,true))
            strings.add("ReadManga");
        if (mSettings.getBoolean(TopManga.APP_SETTINGS_SITE_MINTMANG,false))
            strings.add("MintManga");
        if (mSettings.getBoolean(TopManga.APP_SETTINGS_SITE_SELFMANG,true))
            strings.add("SelfManga");
        strings.add("Mangachan");
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        ListView listView = (ListView) findViewById(R.id.main_site_list);
        // создаем адаптер
        AdapterListSite adapter = new AdapterListSite(this, R.layout.item_list_site, strings);

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameSite = strings.get(position);
                if (nameSite.contains("Read"))
                    readManga();
                else if (nameSite.contains("Mint"))
                    mintManga();
                else if (nameSite.contains("Self"))
                    selfManga();
                else if (nameSite.contains("chan"))
                    mangaChan();
            }
        });
        if (strings.isEmpty()){
            TextView textView = (TextView) findViewById(R.id.main_text_info);
            textView.setVisibility(View.VISIBLE);
        }
        SharedPreferences mSettinggs = getSharedPreferences(TopManga.APP_PREFERENCES, MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = mSettinggs.edit();
        editor.putBoolean(TopManga.APP_PREFERENCES_FIRST,false);
        editor.commit();
        newInten = new Intent(MainActivity.this,TopManga.class);
    }

    public void readManga() {
        //  Intent newInten = new Intent(MainActivity.this,TopManga.class);
         clManga = new ClassMang("http://readmanga.me","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]",70);
         clManga.setWhereAll("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
         newInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
         startActivity(newInten);
    }

    public void mintManga() {
      //  Intent newInten = new Intent(MainActivity.this,TopManga.class);
        clManga = new ClassMang("http://mintmanga.com","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]",70);
        clManga.setWhereAll("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
        newInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newInten);
    }

    public void selfManga() {
        //  Intent newInten = new Intent(MainActivity.this,TopManga.class);
        clManga = new ClassMang("http://selfmanga.ru","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]",70);
        clManga.setWhereAll("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
        newInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newInten);
    }

    public void mangaChan(){
        clManga= new ClassMang("http://mangachan.me","[class=manga_images] img[src]","[class=manga_row1] [class=title_link]","[class=content_row]",10);
        clManga.setWhereAll("/mostfavorites?offset=","",0);
        clManga.setPath2("");
        newInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newInten);
    }

    @Override
    public void onStop() {
        if (clManga != null)
            EventBus.getDefault().post(clManga);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }
}
