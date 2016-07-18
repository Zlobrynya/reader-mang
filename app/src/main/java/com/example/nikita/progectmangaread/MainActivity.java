package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.example.nikita.progectmangaread.DataBasePMR.DatabaseHelper;
import com.example.nikita.progectmangaread.classPMR.classMang;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
    private Intent newInten;
    private classMang clManga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);
        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        newInten = new Intent(MainActivity.this,temple_pase.class);
    }

    public void Click(View view) {
        //Intent newInten = new Intent(MainActivity.this,temple_pase.class);
        clManga = new classMang("http://readmanga.me","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]",70);
        clManga.setWhereAll("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
        startActivity(newInten);
    }

    public void AdultManga(View view) {
      //  Intent newInten = new Intent(MainActivity.this,temple_pase.class);
        clManga = new classMang("http://mintmanga.com","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]",70);
        clManga.setWhereAll("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
        startActivity(newInten);
    }

    public void mangafox(View view) {
        clManga = new classMang("http://mangafox.me","[class=manga_img] img[src]","[class=manga_img] ", "[class=list] li",44);
        clManga.setWhereAll("/directory/", ".html", 1);
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
