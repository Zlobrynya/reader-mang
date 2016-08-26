package com.example.nikita.progectmangaread.Activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassMainTop;
import com.example.nikita.progectmangaread.classPMR.ClassMang;
import com.example.nikita.progectmangaread.classPMR.ClassTransport;
import com.example.nikita.progectmangaread.fragment.fragmentLoad_page0;
import com.example.nikita.progectmangaread.fragment.fragmentGenres;
import com.example.nikita.progectmangaread.fragment.fragmentSearchAndGenres;
import com.example.nikita.progectmangaread.service.AlarmManagerBroadcastReceiver;
import com.example.nikita.progectmangaread.service.UpdateMangBookmark;

import de.greenrobot.event.EventBus;

/**
 * Класс для топа вывода манг, с помощью ViewPager,
 * происходит пролитывания fragments
 *
 *
 */

public class TopManga extends BaseActivity {
    private ClassMang mang;
    private ViewPager pager;
    private AdapterPargerFragment gg;
    private ClassMainTop classTop;
    private boolean doublePressBack = false;

    public static int HEIGHT_WIND,WIDTH_WIND;

    private static final String APP_PREFERENCES = "settingsListMang";
    private static final String APP_PREFERENCES_URL = "URL";
    private static final String APP_PREFERENCES_IMG_URL = "imgURL";
    private static final String APP_PREFERENCES_WHERE = "where";
    private static final String APP_PREFERENCES_WHERE_ALL = "whereAll";
    private static final String APP_PREFERENCES_PATH = "path";
    private static final String APP_PREFERENCES_PATH_2 = "path2";
    private static final String APP_PREFERENCES_NAME_CELL = "nameCell";
    private static final String APP_PREFERENCES_NAME_URL = "nameURL";
    private static final String APP_PREFERENCES_FIRST = "first";
    private static final String APP_PREFERENCES_MAX_IN_PAGE = "maxInPage";
    private SharedPreferences mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_top_mang, frameLayout);
        mSettings = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        pager=(ViewPager)findViewById(R.id.pager);
        gg = new AdapterPargerFragment(getSupportFragmentManager(),3);
        pager.setAdapter(gg);
        pager.setCurrentItem(1);
        boolean first = mSettings.getBoolean(APP_PREFERENCES_FIRST,true);

        if (!first){
            mang = new ClassMang();
            mang.setImgURL(mSettings.getString(APP_PREFERENCES_IMG_URL, ""));
            mang.setWhereAll(mSettings.getString(APP_PREFERENCES_WHERE_ALL, ""));
            mang.setNameURL(mSettings.getString(APP_PREFERENCES_NAME_URL, ""));
            mang.setURL(mSettings.getString(APP_PREFERENCES_URL, ""));
            mang.setNameCell(mSettings.getString(APP_PREFERENCES_NAME_CELL, ""));
            mang.setMaxInPage(mSettings.getInt(APP_PREFERENCES_MAX_IN_PAGE, 0));
            mang.setWhere(mSettings.getString(APP_PREFERENCES_WHERE, ""));
            mang.setPath(mSettings.getString(APP_PREFERENCES_PATH, ""));
            mang.setPath2(mSettings.getString(APP_PREFERENCES_PATH_2, ""));
        }else{
            Intent newInten = new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(newInten);
        }

        if (!isMyServiceRunning(UpdateMangBookmark.class)){
            Log.i("Service","start");
            AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
            alarm.SetAlarm(this,"");
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        HEIGHT_WIND = displaymetrics.heightPixels;
        WIDTH_WIND = displaymetrics.widthPixels;

        //костыль для того что бы пославть EventBuss после создания фрагментов
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mang != null) EventBus.getDefault().post(mang);
            }
        }, 100);

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
        if (classTop != null)
            EventBus.getDefault().post(classTop);
        super.onStop();
    }

   /* public void onEvent(ClassMainTop event){
        Toast toast = Toast.makeText(this,
                event.getURL_characher(), Toast.LENGTH_SHORT);
        toast.show();
        classTop = event;
        Log.i(PROBLEM, "event Top");
    }*/

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service",serviceClass.getName());
                return true;
            }
        }
        return false;
    }


    public void onEvent(ClassMang event){
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(APP_PREFERENCES_URL,event.getURL());
        editor.putString(APP_PREFERENCES_NAME_CELL,event.getNameCell());
        editor.putString(APP_PREFERENCES_IMG_URL,event.getImgURL());
        editor.putString(APP_PREFERENCES_NAME_URL,event.getNameURL());
        editor.putString(APP_PREFERENCES_WHERE, event.getWhere());
        editor.putString(APP_PREFERENCES_PATH,event.getPath());
        editor.putString(APP_PREFERENCES_PATH_2,event.getPath2());
        editor.putString(APP_PREFERENCES_WHERE_ALL,event.getWhereAll());

        editor.putBoolean(APP_PREFERENCES_FIRST,false);
        editor.putInt(APP_PREFERENCES_MAX_IN_PAGE, event.getMaxInPage());
        editor.apply();

        mang = new ClassMang();
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
        if (fragmentManager.getBackStackEntryCount() > 0){
            super.onBackPressed();
            return;
        }
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

    public void onEvent(ClassTransport event) {
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
                case 2:{
                    return fragmentSearchAndGenres.newInstance(position);
                }
            }
            return null;
        }
    }

}
