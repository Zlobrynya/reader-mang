package com.example.nikita.progectmangaread.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterBookmark;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseDownloadMang;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassDescriptionMang;
import com.example.nikita.progectmangaread.classPMR.ClassForList;
import com.example.nikita.progectmangaread.classPMR.ClassRecentlyRead;
import com.example.nikita.progectmangaread.classPMR.ClassTransportForList;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class ShowDownloaded extends BaseActivity {
    private AdapterBookmark adapter;
    private ArrayList<ClassRecentlyRead> list;
    private ClassDataBaseDownloadMang classDataBaseDownloadMang;
    private ListView listView;
    private int pos;
    private boolean delete;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.list_view, frameLayout);
        pos = - 1;
        delete = false;
        list = new ArrayList<>();
        SharedPreferences mSettings = getSharedPreferences(TopManga.APP_SETTINGS, MODE_PRIVATE);
        path = mSettings.getString(TopManga.APP_SETTINGS_PATH, getFilesDir().getAbsolutePath());
        classDataBaseDownloadMang = new ClassDataBaseDownloadMang(this);
        listView = (ListView) this.findViewById(R.id.listView);
        getSupportActionBar().setTitle("Download"); // set the top title

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowDownloaded.this, DescriptionMang.class);
                startActivity(intent);
                pos = position;
            }
        });
        adapter = new AdapterBookmark(this,R.layout.list_view,list);

        initializationDonwload();

    }

    private void initializationDonwload(){
        Cursor cursor = classDataBaseDownloadMang.getDownloadMang();
        Log.i("Cursor: ", String.valueOf(cursor.getCount()));
        cursor.moveToFirst();
        list.clear();
        for(int i = 0;i < cursor.getCount();i++){
            String nameMang,URLchapter;
            nameMang = cursor.getString(cursor.getColumnIndex(ClassDataBaseDownloadMang.NAME_MANG));
            URLchapter = cursor.getString(cursor.getColumnIndex(ClassDataBaseDownloadMang.URL_MANG));
            //Путь до изображения в фотрате: "file://"+getCacheDir().toString()+ "дальнейший путь"
            String imgUrl = "file://" + path + "/" + cursor.getString(cursor.getColumnIndex(ClassDataBaseDownloadMang.NAME_IMG));
            if (!classDataBaseDownloadMang.getDataFromDataBase(nameMang, ClassDataBaseDownloadMang.NAME_CHAPTER).isEmpty())
                list.add(new ClassRecentlyRead(imgUrl,nameMang,"",URLchapter,""));
                    cursor.moveToNext();
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        if (pos > -1 && !delete){
            ClassDescriptionMang classDescriptionMang = new ClassDescriptionMang();
            classDescriptionMang.setCategory(classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.CATEGORY));
            classDescriptionMang.setDescription(classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.DESCRIPTION));
            classDescriptionMang.setGenre(classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.GENRES));
            classDescriptionMang.setImg_url("file://"+path+"/"+classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.NAME_IMG));
            classDescriptionMang.setNameAuthor(classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.AUTHOR));
            classDescriptionMang.setRank(classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.RATING));
            classDescriptionMang.setToms(classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.TOMS));
            classDescriptionMang.setTranslate(classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.TRANSLATION));
            EventBus.getDefault().post(classDescriptionMang);
            EventBus.getDefault().post(creatureClassTransportForList());
        }
        if (delete){
            EventBus.getDefault().post(creatureClassTransportForList());
        }
        delete = false;
        pos = -1;
        // classDataBaseDownloadMang.closeDataBase();
        super.onStop();
    }

    private ClassTransportForList creatureClassTransportForList(){
        ArrayList<ClassForList> forLists = new ArrayList<>();
        String[] names = classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.NAME_CHAPTER).split(",");
        String[] urls = classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.NAME_DIR).split(",");
        for (int i = 0; i < names.length; i++){
            try {
                ClassForList forList = new ClassForList();
                forList.setName_chapter(names[i]);
                forList.setURL_chapter(urls[i]);
                forLists.add(forList);
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
        return new ClassTransportForList(forLists,list.get(pos).getNameMang(),null);
    }

    public void onDestroy(){
        super.onDestroy();
    }

    public void onResume(){
        initializationDonwload();
        super.onResume();
    }

    public void imageButtonDelete(View view) {
        pos = (int) view.getTag();
        if (!classDataBaseDownloadMang.getDataFromDataBase(list.get(pos).getNameMang(), ClassDataBaseDownloadMang.NAME_CHAPTER).isEmpty()){
            Toast.makeText(this, "Delete: " + list.get(pos).getNameMang(),
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ShowDownloaded.this, DeleteChapter.class).putExtra("name",
                    list.get(pos).getNameMang()));
        }
        delete = true;
    }
}
