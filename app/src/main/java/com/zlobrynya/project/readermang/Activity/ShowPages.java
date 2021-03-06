package com.zlobrynya.project.readermang.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.ParsSite.InterParsPageMang;
import com.zlobrynya.project.readermang.ParsSite.tools.HelperParsPageMang;
import com.zlobrynya.project.readermang.ThreadManager;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseViewedHead;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.cacheImage.CacheFile;
import com.zlobrynya.project.readermang.fragment.fragmentNextPrevChapter;
import com.zlobrynya.project.readermang.fragment.fragmentPageDownlad;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Nikita on 07.03.2016.
 */

public class ShowPages extends AppCompatActivity {
    private ArrayList<String> urlPage;
    private int chapterNumber;
    private int pageNumber;
    private String nameMang;
    private String nameChapter;
    private ViewPager pager;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private ProgressBar progress;
    private boolean download;
    private PagerTabStrip pagerTabStrip;
    public ThreadManager threadManager;
    public static String nameDirectory;
    public static String pathDir;
    private final String strLog = "DownloadChapter";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pageview_image);
        // Имя где будут искаться страницы манги. pageCache - для онлайн посмотра остальное
        // для скачанной манги
        nameDirectory = "pageCache";
        download = false;
        SharedPreferences mSettings = getSharedPreferences(TopManga.APP_SETTINGS, MODE_PRIVATE);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setVisibility(View.GONE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        urlPage = new ArrayList<>();
        progress = (ProgressBar) findViewById(R.id.loadingPageView);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerPageImage);
        pagerTabStrip.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progress.getLayoutParams();
        params.setMargins(0, TopManga.HEIGHT_WIND / 2, 0, 0);
        progress.setLayoutParams(params);
        pager = (ViewPager) findViewById(R.id.pagerImage);
        pager.setVisibility(View.GONE);

        //textIdPage = (TextView) findViewById(R.id.textNumberPage);
        final AppCompatActivity activity = this;

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageSelected(int position) {
                if (urlPage.size() != 0) {
                    if (position > 0 && position <= urlPage.size() && !download){
                        threadManager.setPriorityImg(position-1);
                        pageNumber = position;
                        classDataBaseViewedHead.setData(nameMang, String.valueOf(pageNumber), ClassDataBaseViewedHead.LAST_PAGE);
                        return;
                    }
                    if (position == 0) {
                        EventBus.getDefault().postSticky(chapterNumber + 1);
                        activity.finish();
                    }
                    //для манги где всего 1 картинка на главу
                    if (urlPage.size() == 1) {
                        if (position > urlPage.size()) {
                            EventBus.getDefault().postSticky(chapterNumber - 1);
                            activity.finish();
                        }
                    } else {
                        if (position == urlPage.size() + 1) {
                            EventBus.getDefault().postSticky(chapterNumber - 1);
                            activity.finish();
                        }
                    }
                    classDataBaseViewedHead.setData(nameMang, String.valueOf(1), ClassDataBaseViewedHead.LAST_PAGE);
                    EventBus.getDefault().post(position+"/Start");
                }
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               /* if (position > 0 && position <= urlPage.size())
                    textIdPage.setText(position+"/"+(urlPage.size())+"    "+nameChapter);
                if (urlPage.size() == 1)
                    textIdPage.setText(position+"/"+(urlPage.size())+"    "+nameChapter);*/
            }
        });

        InterParsPageMang addImg = new InterParsPageMang() {
            @Override
            public void onEnd(String string) {
                try{
                    if (!string.isEmpty()){
                        nameChapter = string;
                    }

                    progress.setVisibility(View.GONE);
                    pager.setVisibility(View.VISIBLE);
                    classDataBaseViewedHead.setData(nameMang, nameChapter, ClassDataBaseViewedHead.NAME_LAST_CHAPTER);
                    activity.getSupportActionBar().setTitle(nameChapter); // set the top title

                    GalleryAdapter adapter = new GalleryAdapter(getSupportFragmentManager());
                    threadManager = new ThreadManager(urlPage);
                    if (pageNumber > urlPage.size()){
                        pageNumber = 1;
                    }
                    if (pageNumber == urlPage.size() && urlPage.size() != 1){
                        pageNumber = urlPage.size()-1;
                    }
                    pager.setAdapter(adapter);
                    pager.setCurrentItem(pageNumber);
                }catch (IllegalStateException e){
                    Crashlytics.logException(e);
                }
            }
        };
        Intent intent = getIntent();
        String URL = intent.getStringExtra("URL");
        chapterNumber = intent.getIntExtra("NumberChapter", 0);
        pageNumber = intent.getIntExtra("NumberPage",1);
        nameMang = intent.getStringExtra("Manga");
        download = intent.getBooleanExtra("Download",false);
        if (!download){
           // file.clearCache();
            pathDir = getCacheDir().getPath();
            nameChapter = intent.getStringExtra("Chapter");
            classDataBaseViewedHead = new ClassDataBaseViewedHead(this);
            if (URL == null){
                Toast.makeText(this, "Произошла ошибка.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
            HelperParsPageMang parsPageMang = new HelperParsPageMang(URL,urlPage,getApplicationContext());
            parsPageMang.addInterface(addImg);
            parsPageMang.startPars();
        }else {
            pathDir = mSettings.getString(TopManga.APP_SETTINGS_PATH,getFilesDir().getAbsolutePath());
            CacheFile file = new CacheFile(new File(pathDir), URL);
            nameDirectory = URL;
            classDataBaseViewedHead = new ClassDataBaseViewedHead(this);
            nameChapter = intent.getStringExtra("Chapter");
            for (int i = 0; i < file.getNumberOfFile();i++)
                urlPage.add(String.valueOf(i));
            addImg.onEnd(nameChapter);
            Log.i("ShowPages", String.valueOf(file.getNumberOfFile()));
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pager.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_page_download, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }else if (id == R.id.sett_brightness){
                settingsBrightness();
        }else if (id == R.id.sett_page_rotation){
               item = setLockRotate(item);
        }else if (id == R.id.sett_page_reload){
            if (!download){
                CacheFile file = new CacheFile(getCacheDir(), "pageCache");
                file.deleteFile(String.valueOf(pageNumber-1));
                threadManager.setFalseSaveImg(pageNumber-1);
                threadManager.setPriorityImg(pageNumber-1);
                EventBus.getDefault().post(pageNumber-1+"/reload");
            }else EventBus.getDefault().post(pageNumber-1+"/reloadDown");

        }else if (id == R.id.next_page_skip){
            classDataBaseViewedHead.setData(nameMang, String.valueOf(1), ClassDataBaseViewedHead.LAST_PAGE);
            EventBus.getDefault().postSticky(chapterNumber - 1);
            this.finish();
        }else if (id == R.id.previous_page_skip){
            classDataBaseViewedHead.setData(nameMang, String.valueOf(1), ClassDataBaseViewedHead.LAST_PAGE);
            EventBus.getDefault().postSticky(chapterNumber + 1);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //Поворот экрана
    private MenuItem setLockRotate(MenuItem item){
        if (item.getTitle().equals(getResources().getString(R.string.sett_rotation_lock))){
            //Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
            item.setTitle(getResources().getString(R.string.sett_rotation_unlock));
            item.setIcon(R.drawable.ic_screen_rotation_black_24dp);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0); // отключение
        }else {
            item.setTitle(getResources().getString(R.string.sett_rotation_lock));
            item.setIcon(R.drawable.ic_screen_lock_rotation_black_24dp);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            //Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1); //включение
        }
        return item;
    }

    //Настройка яркости
    private void settingsBrightness(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_change_screen_brightness, (ViewGroup) findViewById(R.id.layout_dialog_change_brightness));
        AlertDialog dialog = builder.setTitle(R.string.sett_brightness)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                })
                .setView(layout).create();

        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.show();

        SeekBar yourDialogSeekBar = (SeekBar)layout.findViewById(R.id.dialog_seekbar_brighness);
        //Получаем начальные данные о яркости
        int brightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,0);
        yourDialogSeekBar.setProgress(brightness);
        final ContentResolver context = getContentResolver();

        SeekBar.OnSeekBarChangeListener yourSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //add code here
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //add code here
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
               // Settings.System.putInt(context, Settings.System.SCREEN_BRIGHTNESS_MODE, 0);
               // Settings.System.putInt(context,Settings.System.SCREEN_BRIGHTNESS,progress);
                WindowManager.LayoutParams layout = getWindow().getAttributes();
                layout.screenBrightness = progress;
                getWindow().setAttributes(layout);
            }
        };
        yourDialogSeekBar.setOnSeekBarChangeListener(yourSeekBarListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(java.lang.Short event){
        chapterNumber = event;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(java.lang.String event){
        //Если декодирование сфейлилось то запускаем перезагрузку изображдения
        if (event.contains("FailGetImg")){
            Log.i("PageDonwload","FailEventAccepted");
            threadManager.setFalseSaveImg(Integer.parseInt(event.split("/")[1]));
            threadManager.setPriorityImg(Integer.parseInt(event.split("/")[1]));
            EventBus.getDefault().post(event.split("/")[1]+"/reload");
            EventBus.getDefault().cancelEventDelivery(event);
        }
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(strLog, "Stop");
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_BACK){
            if (!download){
                CacheFile file = new CacheFile(getCacheDir(),"pageCache");
                file.clearCache();
            }
            super.onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void ClickLinearLayout(View view) {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        if(actionBar.isShowing()){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            pagerTabStrip.setVisibility(View.GONE);
            actionBar.hide();
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            pagerTabStrip.setVisibility(View.VISIBLE);
            actionBar.show();
        }
    }

    //FragmentStatePagerAdapter
    private final class GalleryAdapter extends FragmentStatePagerAdapter {

        GalleryAdapter(FragmentManager mgr) {
            super(mgr);
        }

        @Override
        public int getCount() {
            return urlPage.size()+2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return "Previous chapter";
            if (position > urlPage.size()) return "Next chapter";
            if (position <= urlPage.size()) return position+"/"+urlPage.size();
            return "Magic";
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return fragmentNextPrevChapter.getInstance(position,"Previous chapter");
            if (urlPage.size() == 1) {
                if (position > urlPage.size())
                    return fragmentNextPrevChapter.getInstance(position, "Next chapter");
                else
                    return fragmentPageDownlad.getInstance(0, urlPage.get(0));
            }else{
                if (position > urlPage.size()) return fragmentNextPrevChapter.getInstance(position,"Next chapter");
                if (position <= urlPage.size()) return fragmentPageDownlad.getInstance(position-1,urlPage.get(position-1));
            }

            return fragmentNextPrevChapter.getInstance(position,"Magic");
        }
    }

    @Override
    public void onDestroy() {
        Log.i("Destroy:", String.valueOf("PageDowland"));
        if (threadManager != null)
                threadManager.stop();
      /*  if (classDataBaseViewedHead != null)
            classDataBaseViewedHead.closeDataBase();*/
        super.onDestroy();
    }


}
