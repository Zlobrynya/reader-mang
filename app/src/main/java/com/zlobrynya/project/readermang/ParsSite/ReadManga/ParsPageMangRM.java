package com.zlobrynya.project.readermang.ParsSite.ReadManga;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.Activity.ShowPages;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseViewedHead;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Nikita on 30.01.2017.
 */

public class ParsPageMangRM {
    //поток для скачивания сылок для изображений
    public class ParsURLPage extends AsyncTask<Void,Void,Void> {
        private Document doc;
        private AsyncTaskLisen asyncTask;
        private String html;
        private boolean not_net;
        //конструктор потока
        ParsURLPage(AsyncTaskLisen addImg, String url) {
            not_net = false;
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
                if (doc == null)
                    doc = Jsoup.connect(URL)
                            .userAgent("Mozilla")
                            .timeout(60000)
                            .get();
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
                not_net = true;
                Log.i("pageDowload","Не грузит страницу либо больше нечего грузить");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //           TextView textView = (TextView) findViewById(R.id.text);
            try{
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
                    Toast.makeText(ShowPages.this, "Что то с инетом", Toast.LENGTH_SHORT).show();
                }
            }catch (NullPointerException e){
                Crashlytics.logException(e);
                Crashlytics.setString("mangUrl",URL);
            }catch (Exception e ){
                Crashlytics.logException(e);
                Crashlytics.log("mangUrl"+URL);
            }
        }
    }
}
