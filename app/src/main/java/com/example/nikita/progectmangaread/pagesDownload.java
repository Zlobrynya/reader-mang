package com.example.nikita.progectmangaread;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseViewedHead;
import com.example.nikita.progectmangaread.cacheImage.cacheFile;
import com.example.nikita.progectmangaread.fragment.fragmentNextPrevChapter;
import com.example.nikita.progectmangaread.fragment.fragmentPageDownlad;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;


/**
 * Created by Nikita on 07.03.2016.
 *
 * Продумать норм загрузку стр (показать что качается)
 *
 */

public class pagesDownload extends AppCompatActivity {
    private ArrayList<String> urlPage;
    private int chapterNumber,pageNumber;
    private TextView textIdPage;
    private String URL,nameMang,nameChapter;
    private ViewPager pager;
    private classDataBaseViewedHead classDataBaseViewedHead;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pageview_image);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        urlPage = new ArrayList<>();
        progress = (ProgressBar) findViewById(R.id.loading);


        pager = (ViewPager) findViewById(R.id.pagerImage);
        pager.setVisibility(View.GONE);

        textIdPage = (TextView) findViewById(R.id.textNumberPage);
        final AppCompatActivity activity = this;

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageSelected(int position) {
                if (urlPage.size() != 0){
                    if (position == 0) {
                        //по идеи это глава которая была раньше, но так, как лист идет сверху вниз (от самой последней в
                        //      самую старую, (потом это переделать))
                        EventBus.getDefault().post(chapterNumber + 1);
                        cacheFile file = new cacheFile(getCacheDir(),"pageCache");
                        file.clearCache();
                        activity.finish();
                    }
                    if (position == urlPage.size()){
                        EventBus.getDefault().post(chapterNumber - 1);
                        cacheFile file = new cacheFile(getCacheDir(),"pageCache");
                        file.clearCache();
                        activity.finish();
                    }
                    pageNumber = position;
                    classDataBaseViewedHead.setData(nameMang, String.valueOf(pageNumber), classDataBaseViewedHead.LAST_PAGE);
                }
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position > 0 && position < urlPage.size())
                        textIdPage.setText(position+"/"+(urlPage.size()-1)+"    "+nameChapter);
            }
        });
        AsyncTaskLisen addImg = new AsyncTaskLisen() {
            @Override
            public void onEnd() {
                GalleryAdapter adapter = new GalleryAdapter(getSupportFragmentManager());
                if (pageNumber > urlPage.size()){
                    pageNumber = 1;
                }
                if (pageNumber == urlPage.size()){
                    pageNumber = urlPage.size()-1;
                }
                pager.setAdapter(adapter);
                pager.setCurrentItem(pageNumber);
            }

            @Override
            public void onEnd(InputStream is) {

            }
        };
        Intent intent = getIntent();
        URL = intent.getStringExtra("URL");
        chapterNumber = intent.getIntExtra("NumberChapter", 0);
        pageNumber = Integer.parseInt(intent.getStringExtra("NumberPage"));
        nameMang = intent.getStringExtra("Chapter");
        classDataBaseViewedHead = new classDataBaseViewedHead(this);

        ParsURLPage par = new ParsURLPage(addImg,URL);
        par.execute();
    }

    //Для фрагментов
    public void onEvent(String event){
        if (event.contains("CkickImage")){
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            if(actionBar.isShowing()){
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                actionBar.hide();
            }else{
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                actionBar.show();
            }
        }
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
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        cacheFile file = new cacheFile(getCacheDir(),"pageCache");
        file.clearCache();
        super.onBackPressed();
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
        public Fragment getItem(int position) {
            if (position == 0) return fragmentNextPrevChapter.getInstance(position,"Previous chapter");
            if (position >= urlPage.size()) return fragmentNextPrevChapter.getInstance(position,"Next chapter");
            if (position < urlPage.size()) return fragmentPageDownlad.getInstance(position-1,urlPage.get(position-1));
            else return null;
        }

    }

    @Override
    public void onDestroy() {
        Log.i("Destroy:", String.valueOf("PageDowland"));
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
                String second = "";
             //   Log.i("Strign firdt: ", secondBuffer.substring(secondBuffer.indexOf("init"), secondBuffer.lastIndexOf("false")));
                second = secondBuffer.substring(secondBuffer.indexOf("init") + 6, secondBuffer.lastIndexOf("false") - 4);
                second = second.replace("[","");
                second = second.replace("]","");
                String[] test = second.split(",");

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

                classDataBaseViewedHead.setData(nameMang, nameChapter, classDataBaseViewedHead.NAME_LAST_CHAPTER);
                asyncTask.onEnd();
            }else{
                Toast.makeText(pagesDownload.this, "Что то с инетом", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
