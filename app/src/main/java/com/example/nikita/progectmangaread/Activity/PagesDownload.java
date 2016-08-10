package com.example.nikita.progectmangaread.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Build;
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

import com.example.nikita.progectmangaread.ThreadManager;
import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.cacheImage.CacheFile;
import com.example.nikita.progectmangaread.fragment.fragmentNextPrevChapter;
import com.example.nikita.progectmangaread.fragment.fragmentPageDownlad;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;


/**
 * Created by Nikita on 07.03.2016.
 */

public class PagesDownload extends AppCompatActivity {
    private ArrayList<String> urlPage;
    private int chapterNumber,pageNumber;
    private TextView textIdPage;
    private String URL,nameMang,nameChapter;
    private ViewPager pager;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private ProgressBar progress;
    private boolean download;
    private SharedPreferences mSettings;
    private PagerTabStrip pagerTabStrip;
    public ThreadManager threadManager;
    public static String nameDirectory,pathDir;
    private final String strLog = "DownloadChapter";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pageview_image);

        CacheFile file = new CacheFile(getCacheDir(), "pageCache");
        file.clearCache();

        //Имя где будут искаться страницы манги. pageCache - для онлайн посмотра остальное
        // для скачанной манги
        nameDirectory = "pageCache";
        download = false;
        mSettings = getSharedPreferences(MainSettings.APP_SETTINGS, MODE_PRIVATE);

       /* android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();*/
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
                    if (position > 0 && position <= urlPage.size() && !download)
                        threadManager.setPriorityImg(position-1);
                    if (position == 0) {
                        //по идеи это глава которая была раньше, но так, как лист идет сверху вниз (от самой последней в
                        //      самую старую, (потом это переделать))
                        EventBus.getDefault().post(chapterNumber + 1);
                    //    CacheFile file = new CacheFile(getCacheDir(), "pageCache");
                    //    file.clearCache();
                        activity.finish();
                    }
                    //для манги где всего 1 картинка на главу
                    if (urlPage.size() == 1) {
                        if (position > urlPage.size()) {
                            EventBus.getDefault().post(chapterNumber - 1);
                    //        CacheFile file = new CacheFile(getCacheDir(), "pageCache");
                   //         file.clearCache();
                            activity.finish();
                        }
                    } else {
                        if (position == urlPage.size() + 1) {
                            EventBus.getDefault().post(chapterNumber - 1);
                    //        CacheFile file = new CacheFile(getCacheDir(), "pageCache");
                    //        file.clearCache();
                            activity.finish();
                        }
                    }
                    pageNumber = position;
                    if (!download)
                        classDataBaseViewedHead.setData(nameMang, String.valueOf(pageNumber), ClassDataBaseViewedHead.LAST_PAGE);
                }
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               /* if (position > 0 && position <= urlPage.size())
                    textIdPage.setText(position+"/"+(urlPage.size())+"    "+nameChapter);
                if (urlPage.size() == 1)
                    textIdPage.setText(position+"/"+(urlPage.size())+"    "+nameChapter);*/
            }
        });
        AsyncTaskLisen addImg = new AsyncTaskLisen() {
            @Override
            public void onEnd() {
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
            }

            @Override
            public void onEnd(int number) {

            }
        };
        Intent intent = getIntent();
        URL = intent.getStringExtra("URL");
        chapterNumber = intent.getIntExtra("NumberChapter", 0);
        pageNumber = Integer.parseInt(intent.getStringExtra("NumberPage"));
        nameMang = intent.getStringExtra("Chapter");
        download = intent.getBooleanExtra("Download",false);
        if (!download){
            pathDir = getCacheDir().getPath();
            classDataBaseViewedHead = new ClassDataBaseViewedHead(this);
            ParsURLPage par = new ParsURLPage(addImg,URL);
            par.execute();
        }else {
            pathDir = mSettings.getString(MainSettings.APP_SETTINGS_PATH,getFilesDir().getAbsolutePath());
            file = new CacheFile(new File(pathDir),URL);
            nameDirectory = URL;
            nameChapter = intent.getStringExtra("Chapter");
            for (int i = 0; i < file.getNumberOfFile();i++)
                urlPage.add(String.valueOf(i));
            progress.setVisibility(View.GONE);
            pager.setVisibility(View.VISIBLE);
            addImg.onEnd();
            getSupportActionBar().setTitle(nameChapter); // set the top title
            Log.i("PagesDownload", String.valueOf(file.getNumberOfFile()));
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(PagesDownload.this)) {
                    settingsBrightness();
                }else {
                    setPermissionBrightness();
                }
            }else {
                settingsBrightness();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setPermissionBrightness(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.info_prermission)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent grantIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        startActivity(grantIntent);
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
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
       /* Dialog dialogBrightness = new Dialog(this);
        dialogBrightness.setTitle(R.string.sett_brightness);
        View layout = inflater.inflate(R.layout.dialog_change_screen_brightness, (ViewGroup) findViewById(R.id.layout_dialog_change_brightness));
        dialogBrightness.setContentView(layout);
        dialogBrightness.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogBrightness.show();*/

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
                Settings.System.putInt(context,Settings.System.SCREEN_BRIGHTNESS,progress);
            }
        };
        yourDialogSeekBar.setOnSeekBarChangeListener(yourSeekBarListener);
    }

    public void onEvent(java.lang.Short event){
        chapterNumber = event;
    }


    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i(strLog, "Stop");
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (!download){
            CacheFile file = new CacheFile(getCacheDir(),"pageCache");
            file.clearCache();
        }
        super.onBackPressed();
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
                    return fragmentPageDownlad.getInstance(1, urlPage.get(0));
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
        if (classDataBaseViewedHead != null)
            classDataBaseViewedHead.closeDataBase();
        super.onDestroy();
    }

    //поток для скачивания сылок для изображений
    public class ParsURLPage extends AsyncTask<Void,Void,Void> {
        private Document doc;
        private AsyncTaskLisen asyncTask;
        private String html;
        private boolean not_net;
        //конструктор потока
        protected ParsURLPage(AsyncTaskLisen addImg,String url) {
            asyncTask = addImg;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Пост запрос
            try {
                //Запрос на получение сылок для изображений вот он:
                if (doc == null) doc = Jsoup.connect(URL).get();
                nameChapter = doc.select("[class = pageBlock container]").select("h1").text();
                //pageBlock container
                Elements scripts = doc.select("body").select("script");
                for (Element script : scripts){
                    if (script.data().contains("transl_next_page")){
                        html = script.data();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                not_net = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("pageDowload","Не грузит страницу либо больше нечего грузить");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
 //           TextView textView = (TextView) findViewById(R.id.text);
            if (!not_net){
                StringBuilder secondBuffer = new StringBuilder(html);
              //  Log.i("Strign firdt: ", String.valueOf(secondBuffer.lastIndexOf("init")));
              //  Log.i("Strign false: ", String.valueOf(secondBuffer.lastIndexOf("false")));
                String stringBuffer = "";
             //   Log.i("Strign firdt: ", secondBuffer.substring(secondBuffer.indexOf("init"), secondBuffer.lastIndexOf("false")));
                stringBuffer = secondBuffer.substring(secondBuffer.indexOf("init") + 6, secondBuffer.lastIndexOf("false") - 4);
                stringBuffer = stringBuffer.replace("[","");
                stringBuffer = stringBuffer.replace("]","");
                String[] test = stringBuffer.split(",");

                String[] URLhelp;
                URLhelp = new String[3];
                int kol = 0;
                for(String tt: test){
                    if (tt.contains("'")){
                        URLhelp[kol] = tt.substring(tt.indexOf("'")+1,tt.lastIndexOf("'"));
                        kol++;
                    }else if (tt.contains("\"")){
                        URLhelp[2] = tt.substring(tt.indexOf("\"")+1,tt.lastIndexOf("\""));
                        kol++;
                    }
                    if (kol == 3){
                        kol = 0;
                        urlPage.add(URLhelp[1] + URLhelp[0] + URLhelp[2]);
                    }
                }
                progress.setVisibility(View.GONE);
                pager.setVisibility(View.VISIBLE);
                classDataBaseViewedHead.setData(nameMang, nameChapter, ClassDataBaseViewedHead.NAME_LAST_CHAPTER);
                getSupportActionBar().setTitle(nameChapter); // set the top title
                asyncTask.onEnd();
            }else{
                Toast.makeText(PagesDownload.this, "Что то с инетом", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
