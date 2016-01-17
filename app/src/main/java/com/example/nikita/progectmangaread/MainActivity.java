package com.example.nikita.progectmangaread;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {
    private Intent newInten;
    private classMang clManga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        newInten = new Intent(MainActivity.this,temple_pase.class);
    }

    public void Click(View view) {
        //Intent newInten = new Intent(MainActivity.this,temple_pase.class);
        clManga = new classMang("http://readmanga.me","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]",70);
        clManga.setWhere("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
        startActivity(newInten);
    }

    public void AdultManga(View view) {
      //  Intent newInten = new Intent(MainActivity.this,temple_pase.class);
        clManga = new classMang("http://AdultManga.ru","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]",70);
        clManga.setWhere("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
        startActivity(newInten);
    }

    public void mangafox(View view) {
        clManga = new classMang("http://mangafox.me","[class=manga_img] img[src]","[class=manga_img] ", "[class=list] li",44);
        clManga.setWhere("/directory/", ".html",1);
        startActivity(newInten);
    }


    @Override
    public void onStop() {
        EventBus.getDefault().post(clManga);
        super.onStop();
    }
}
