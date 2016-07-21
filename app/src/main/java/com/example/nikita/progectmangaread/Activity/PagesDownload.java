package com.example.nikita.progectmangaread.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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


import java.io.IOException;
import java.io.InputStream;
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
    public static String nameDirectory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pageview_image);

        //Имя где будут искаться страницы манги. pageCache - для онлайн посмотра остальное
        // для скачанной манги
        nameDirectory = "pageCache";
        download = false;

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        urlPage = new ArrayList<>();
        progress = (ProgressBar) findViewById(R.id.loadingPageView);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progress.getLayoutParams();
        params.setMargins(0, TopManga.HEIGHT_WIND/2, 0, 0);
        progress.setLayoutParams(params);
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
                        CacheFile file = new CacheFile(getCacheDir(),"pageCache");
                        file.clearCache();
                        activity.finish();
                    }
                    //для манги где всего 1 картинка на главу
                    if (urlPage.size() == 1){
                        if (position > urlPage.size()){
                            EventBus.getDefault().post(chapterNumber - 1);
                            CacheFile file = new CacheFile(getCacheDir(),"pageCache");
                            file.clearCache();
                            activity.finish();
                        }
                    }else{
                        if (position == urlPage.size()){
                            EventBus.getDefault().post(chapterNumber - 1);
                            CacheFile file = new CacheFile(getCacheDir(),"pageCache");
                            file.clearCache();
                            activity.finish();
                        }
                    }
                    pageNumber = position;
                    if (!download)
                        classDataBaseViewedHead.setData(nameMang, String.valueOf(pageNumber), classDataBaseViewedHead.LAST_PAGE);
                }
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position > 0 && position < urlPage.size())
                    textIdPage.setText(position+"/"+(urlPage.size()-1)+"    "+nameChapter);
                if (urlPage.size() == 1)
                    textIdPage.setText(position+"/"+(urlPage.size())+"    "+nameChapter);

            }
        });
        AsyncTaskLisen addImg = new AsyncTaskLisen() {
            @Override
            public void onEnd() {
                GalleryAdapter adapter = new GalleryAdapter(getSupportFragmentManager());
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
            public void onEnd(InputStream is) {

            }
        };
        Intent intent = getIntent();
        URL = intent.getStringExtra("URL");
        chapterNumber = intent.getIntExtra("NumberChapter", 0);
        pageNumber = Integer.parseInt(intent.getStringExtra("NumberPage"));
        nameMang = intent.getStringExtra("Chapter");
        download = intent.getBooleanExtra("Download",false);
        if (!download){
            classDataBaseViewedHead = new ClassDataBaseViewedHead(this);
            ParsURLPage par = new ParsURLPage(addImg,URL);
            par.execute();
        }else {
            CacheFile file = new CacheFile(getCacheDir(),URL);
            nameDirectory = URL;
            nameChapter = intent.getStringExtra("Chapter");
            for (int i = 0; i < file.getNumberOfFile();i++)
                urlPage.add(String.valueOf(i));
            progress.setVisibility(View.GONE);
            pager.setVisibility(View.VISIBLE);
            addImg.onEnd();
            Log.i("PagesDownload", String.valueOf(file.getNumberOfFile()));
        }
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
        if (!download){
            CacheFile file = new CacheFile(getCacheDir(),"pageCache");
            file.clearCache();
        }
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
            if (urlPage.size() == 1) {
                if (position > urlPage.size())
                    return fragmentNextPrevChapter.getInstance(position, "Next chapter");
                else
                    return fragmentPageDownlad.getInstance(1, urlPage.get(0));
            }else{
                if (position >= urlPage.size()) return fragmentNextPrevChapter.getInstance(position,"Next chapter");
                if (position < urlPage.size()) return fragmentPageDownlad.getInstance(position-1,urlPage.get(position-1));
            }

            if (urlPage.size() == 1) return fragmentPageDownlad.getInstance(1,urlPage.get(0));
            else return null;
        }

    }

    @Override
    public void onDestroy() {
        Log.i("Destroy:", String.valueOf("PageDowland"));
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
                classDataBaseViewedHead.setData(nameMang, nameChapter, classDataBaseViewedHead.NAME_LAST_CHAPTER);
                asyncTask.onEnd();
            }else{
                Toast.makeText(PagesDownload.this, "Что то с инетом", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
