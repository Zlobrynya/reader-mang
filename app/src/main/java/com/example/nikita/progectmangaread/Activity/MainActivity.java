package com.example.nikita.progectmangaread.Activity;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;

import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassMang;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
    private Intent newInten;
    private ClassMang clManga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        newInten = new Intent(MainActivity.this,TopManga.class);
    }

    public void Click(View view) {
        //  Intent newInten = new Intent(MainActivity.this,TopManga.class);
        clManga = new ClassMang("http://readmanga.me","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]",70);
        clManga.setWhereAll("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
        newInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newInten);
    }

    public void AdultManga(View view) {
      //  Intent newInten = new Intent(MainActivity.this,TopManga.class);
        clManga = new ClassMang("http://mintmanga.com","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]",70);
        clManga.setWhereAll("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
        newInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newInten);
    }

    public void mangafox(View view) {
/*        clManga = new ClassMang("http://mangafox.me","[class=manga_img] img[src]","[class=manga_img] ", "[class=list] li",44);
        clManga.setWhereAll("/directory/", ".html", 1);
        startActivity(newInten);*/
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
