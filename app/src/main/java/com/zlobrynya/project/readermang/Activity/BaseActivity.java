package com.zlobrynya.project.readermang.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.zlobrynya.project.readermang.R;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected RelativeLayout frameLayout;
    private boolean doublePressBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        frameLayout = (RelativeLayout)findViewById(R.id.content_frame);
       // Multidex.install(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        startActivity(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startActivity(int id){
        if (id == R.id.nav_list_Site) {
            Intent newInten = new Intent(BaseActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(newInten);
        } else if (id == R.id.nav_top_mang) {
            Intent newInten = new Intent(BaseActivity.this,TopManga.class);
            newInten.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newInten);
        } else if (id == R.id.nav_recently_read) {
            Intent newInten = new Intent(BaseActivity.this,RecentlyRead.class);
            startActivity(newInten);
        } else if (id == R.id.nav_bookmark){
            Intent newInten = new Intent(BaseActivity.this,Bookmark.class);
            startActivity(newInten);
        } else if (id == R.id.nav_download_chapter){
            Intent newInten = new Intent(BaseActivity.this,ShowDownloaded.class);
            startActivity(newInten);
        } else if (id == R.id.nav_settings){
            Intent newInten = new Intent(BaseActivity.this,MainSettings.class);
            startActivity(newInten);
        }
    }
}
