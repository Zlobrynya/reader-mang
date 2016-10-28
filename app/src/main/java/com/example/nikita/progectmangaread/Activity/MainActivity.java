package com.example.nikita.progectmangaread.Activity;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterListSite;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassMang;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
    private Intent newInten;
    private ClassMang clManga;
    private AdapterListSite adapter;

    String[] namesSite = { "ReadManga", "MintManga","SelfManga"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("ReadManga");
        strings.add("MintManga");
        strings.add("SelfManga");
        getLayoutInflater().inflate(R.layout.list_view, frameLayout);
        ListView listView = (ListView) findViewById(R.id.listView);
        // создаем адаптер
        adapter = new AdapterListSite(this,R.layout.item_list_site,strings);

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        readManga();
                        break;
                    case 1:
                        mintManga();
                        break;
                    case 2:
                        selfManga();
                        break;
                }
            }
        });
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

    @Override
    public void onStop() {
        if (clManga != null) EventBus.getDefault().post(clManga);
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
