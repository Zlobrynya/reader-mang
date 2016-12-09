package com.zlobrynya.project.readermang.Activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zlobrynya.project.readermang.AdapterPMR.AdapterListChapter;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.classPMR.ClassForList;

import java.util.ArrayList;

/**
 * Created by Nikita on 29.10.2016.
 * Активити для выбора сайтов на которые можно будет перейти
 */

public class SelectionMangSite extends AppCompatActivity {
    private ArrayList<ClassForList> list;
    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Selection site"); // set the top title

        mSettings = getSharedPreferences(TopManga.APP_SETTINGS, MODE_PRIVATE);
        list = new ArrayList<>();
        final AdapterListChapter myAdap = new AdapterListChapter(this, R.layout.list_view_checkbox, list);
        setSitToList("ReadManga");
        setSitToList("MintManga");
        setSitToList("SelfManga");
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(myAdap);
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassForList classForList = list.get(position);
                classForList.setCheck(!classForList.isCheck());
                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = mSettings.edit();
                if (classForList.getNameChapter().contains("Read"))
                    editor.putBoolean(TopManga.APP_SETTINGS_SITE_READMANG,classForList.isCheck());
                else if (classForList.getNameChapter().contains("Mint"))
                    editor.putBoolean(TopManga.APP_SETTINGS_SITE_MINTMANG,classForList.isCheck());
                else if (classForList.getNameChapter().contains("Self"))
                    editor.putBoolean(TopManga.APP_SETTINGS_SITE_SELFMANG,classForList.isCheck());
                list.set(position,classForList);
                myAdap.notifyDataSetChanged();
                editor.apply();
            }
        });
    }

    private void setSitToList(String name){
        ClassForList forList = new ClassForList();
        forList.setNameChapter(name);
        if (name.contains("Read"))
            forList.setCheck(mSettings.getBoolean(TopManga.APP_SETTINGS_SITE_READMANG,true));
        else if (name.contains("Mint"))
            forList.setCheck(mSettings.getBoolean(TopManga.APP_SETTINGS_SITE_MINTMANG,false));
        else if (name.contains("Self"))
            forList.setCheck(mSettings.getBoolean(TopManga.APP_SETTINGS_SITE_SELFMANG,true));
        list.add(forList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
