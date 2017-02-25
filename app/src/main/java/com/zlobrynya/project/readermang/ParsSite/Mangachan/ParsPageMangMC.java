package com.zlobrynya.project.readermang.ParsSite.Mangachan;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.ParsSite.InterParsPageMang;
import com.zlobrynya.project.readermang.ParsSite.ParsPageMang;
import com.zlobrynya.project.readermang.ParsSite.ReadManga.ParsPageMangRM;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nikita on 21.02.2017.
 */

public class ParsPageMangMC extends ParsPageMang {

    public ParsPageMangMC(String URL, ArrayList<String> urlPage, Context context) {
        super(URL, urlPage, context);
    }

    public void startPars(){
        ParsURLPage par = new ParsURLPage(addImg,URL);
        par.execute();
    }

    private class ParsURLPage extends AsyncTask<Void,Void,Void> {
        private String nameChapter;
        private String errorMassage;
        private Document doc;
        private InterParsPageMang asyncTask;
        private String html;
        private boolean not_net;
        private String URL;

        //конструктор потока
        ParsURLPage(InterParsPageMang addImg, String url) {
            not_net = false;
            asyncTask = addImg;
            this.URL = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Пост запрос
            try {
                if (doc == null) {
                    Connection.Response response = Jsoup.connect(URL)
                            ///5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.2
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                            .timeout(100000)
                            .ignoreHttpErrors(true)
                            .execute();
                    int statusCode = response.statusCode();
                    if (statusCode == 200) {
                        doc = Jsoup.connect(URL)
                                .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                                .timeout(100000)
                                .get();
                    } else {
                        errorMassage = response.statusMessage() + " : " + response.statusCode();
                        not_net = true;
                        return null;
                    }
                    //

                    Elements scripts = doc.select("script");
                    for (Element script : scripts){
                        if (script.data().contains("var_news_id ")){
                            html = script.data();
                            int index = html.indexOf("fullimg");
                            html = html.substring(index);
                            Log.i("length", String.valueOf(html.length()));
                            index = html.indexOf("\n");
                            html = html.substring(10,index-2);
                            Log.i("length", String.valueOf(html.length()));
                            break;
                        }
                    }
                   // Log.i("JSON",html);
                    String[] url = html.split(",");
                    for (String anUrl : url) {
                        urlPage.add(anUrl.replace("\"","").replace("\"",""));
                    }
                    nameChapter = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
                not_net = true;
            } catch (Exception e) {
                e.printStackTrace();
                not_net = true;
                Log.i("pageDowload","Не грузит страницу, либо больше нечего грузить");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //           TextView textView = (TextView) findViewById(R.id.text);
            try{
                if (!not_net){
                    asyncTask.onEnd(nameChapter);
                }else{
                    Toast.makeText(context, errorMassage, Toast.LENGTH_SHORT).show();
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
