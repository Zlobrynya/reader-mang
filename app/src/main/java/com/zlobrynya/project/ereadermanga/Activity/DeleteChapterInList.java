package com.zlobrynya.project.ereadermanga.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.zlobrynya.project.ereadermanga.AdapterPMR.AdapterList;
import com.zlobrynya.project.ereadermanga.DataBasePMR.ClassDataBaseDownloadMang;
import com.zlobrynya.project.ereadermanga.R;
import com.zlobrynya.project.ereadermanga.classPMR.ClassForList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nikita on 25.11.2016.
 */

public class DeleteChapterInList extends AppCompatActivity {
    private ArrayList<ClassForList> list;
    private ArrayList<String> dirMang, nameChapters;
    private AdapterList myAdap;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_chapter);
        list = new ArrayList<>();
        dirMang = new ArrayList<>();
        nameChapters = new ArrayList<>();
        myAdap = new AdapterList(this, R.layout.list_view_checkbox, list);
        final ListView listView = (ListView) findViewById(R.id.listChapter);
        listView.setAdapter(myAdap);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Delete chapter"); // set the top title

        Intent intent = getIntent();
        final String nameMang = intent.getStringExtra("name");

        SharedPreferences mSettings = getSharedPreferences(TopManga.APP_SETTINGS, MODE_PRIVATE);
        path = mSettings.getString(TopManga.APP_SETTINGS_PATH,getFilesDir().getAbsolutePath());

        final ClassDataBaseDownloadMang classDataBaseDownloadMang = new ClassDataBaseDownloadMang(this);
        String[] strings = classDataBaseDownloadMang.getDataFromDataBase(nameMang,ClassDataBaseDownloadMang.NAME_DIR).split(",");
        Collections.addAll(dirMang, strings);
        strings = classDataBaseDownloadMang.getDataFromDataBase(nameMang,ClassDataBaseDownloadMang.NAME_CHAPTER).split(",");
        Collections.addAll(nameChapters, strings);

        for (String s: strings){
            ClassForList classForList = new ClassForList();
            classForList.setNameChapter(s);
            classForList.setDownload(true);
            list.add(classForList);
        }

        Button button = (Button) findViewById(R.id.download_btn);
        button.setText("Удалить");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ClassForList classForList = list.get(position);
                classForList.setCheck(!classForList.isCheck());
                list.set(position,classForList);
                myAdap.notifyDataSetChanged();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int pos = 0; pos < list.size(); pos++){
                    ClassForList forList = list.get(pos);
                    if (forList.isCheck())
                        deleteChapter(dirMang.get(pos));
                }
                if (dirMang.isEmpty()){
                    classDataBaseDownloadMang.deletRow(nameMang);
                }else {
                    String dir = "";
                    String nameChapter = "";
                    for (int pos = 0; pos < list.size(); pos++){
                        ClassForList forList = list.get(pos);
                        if (!forList.isCheck()){
                            dir += dirMang.get(pos) + ",";
                            nameChapter += nameChapters.get(pos) + ",";
                        }
                    }
                    classDataBaseDownloadMang.setData(nameMang,dir,ClassDataBaseDownloadMang.NAME_DIR);
                    classDataBaseDownloadMang.setData(nameMang,nameChapter,ClassDataBaseDownloadMang.NAME_CHAPTER);
                    Toast.makeText(DeleteChapterInList.this, "Удаление завершено.", Toast.LENGTH_SHORT).show();
                    DeleteChapterInList.this.finish();
                }
            }
        });

        myAdap.notifyDataSetChanged();
    }

    private void deleteChapter(String nameDir){
        File dirs= new File(path+"/"+nameDir);
        for(File chapter: dirs.listFiles()){
            chapter.delete();
        }
        dirs.delete();
    }

    //Кнопка очистка выбора
    public void ClickDeleteCheck(View view) {
        for (int i = 0; i < list.size();i++){
            ClassForList forList = list.get(i);
            if (forList.isCheck()){
                forList.setCheck(false);
                list.set(i, forList);
                myAdap.notifyDataSetChanged();
            }
        }
    }

    //Кнопка выбора их всего списка
    public void CheckAllChapter(View view) {
        for (int i = 0; i < list.size();i++){
            ClassForList forList = list.get(i);
            forList.setCheck(true);
            list.set(i, forList);
            myAdap.notifyDataSetChanged();
        }
    }
}
