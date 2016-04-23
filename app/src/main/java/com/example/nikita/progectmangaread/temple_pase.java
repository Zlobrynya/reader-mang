package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classMang;
import com.example.nikita.progectmangaread.classPMR.classTransport;
import com.example.nikita.progectmangaread.fragment.fragmentLoad_page0;
import com.example.nikita.progectmangaread.fragment.fragmentGenres;
import com.example.nikita.progectmangaread.fragment.fragmentSearchAndGenres;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.ErrorDialogManager;

/**
 * Класс для топа вывода манг, с помощью ViewPager,
 * происходит пролитывания fragments
 *
 *
 */

public class temple_pase extends BaseActivity {
    private classMang mang;
    private ViewPager pager;
    private AdapterPargerFragment gg;
    private MainClassTop classTop;
    private boolean doublePressBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_temple_pase, frameLayout);

        pager=(ViewPager)findViewById(R.id.pager);
        gg = new AdapterPargerFragment(getSupportFragmentManager(),3);
        pager.setAdapter(gg);
        pager.setCurrentItem(1);
    }

    protected void onNewIntent(Intent intent){
        Log.i("NEW", "INTENT");
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        if (classTop != null) EventBus.getDefault().post(classTop);
        super.onStop();
    }

    public void onEvent(MainClassTop event){
        Toast toast = Toast.makeText(this,
                event.getURL_characher(), Toast.LENGTH_SHORT);
        toast.show();
        classTop = event;
        Intent intent = new Intent(temple_pase.this,DescriptionMang.class);
        startActivity(intent);
    }

    public void onEvent(classMang event){
        mang = new classMang();
        mang = event;
    }

    @Override
    public void onBackPressed() {
        if (doublePressBack){
            super.onBackPressed();
            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.i("Fragment", String.valueOf(fragmentManager.getBackStackEntryCount()));
        doublePressBack = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        //таймер для сброса двойного нажатия назад
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doublePressBack=false;
            }
        }, 2000);

    }

    public void onEvent(classTransport event) {
        pager.setCurrentItem(1);
    }

    public class AdapterPargerFragment extends FragmentPagerAdapter  {
        int kol;
        public AdapterPargerFragment(FragmentManager mgr, int kol) {
            super(mgr);
            this.kol = kol;
        }

        @Override
        public int getCount() {
            return(kol);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Жанры";
                case 1:
                    return "Манга";
                case 2:
                    return "Поиск";
                default:
                    return "Magic";
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return fragmentGenres.newInstance(position);
                case 1:
                    return fragmentLoad_page0.newInstance(position);
                case 2:
                    return fragmentSearchAndGenres.newInstance(position);
            }
            return null;
        }
    }

}
