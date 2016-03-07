package com.example.nikita.progectmangaread;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Nikita on 07.03.2016.
 *
 * Сделать что то типо ViewPager для изображений
 * Сделать скачивание изображений, ну и продумать как скачивать
 */
public class pagesDownload extends Activity {
    ArrayList<String> urlPage;
    ArrayList<Bitmap> image;
    public int imageNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_load);
        urlPage = new ArrayList<>();
        image = new ArrayList<>();
    }
    //поток для скачивания сылок для изображений
    public class ParsURLPage extends AsyncTask<Void,Void,Void> {
        String url;
        Document doc;
        Element script;
        String html;
        //конструктор потока
        protected ParsURLPage(String url) {
            this.url = url;
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
                if (doc == null) doc = Jsoup.connect("http://readmanga.me/the_basketball_which_kuroko_plays/vol1/1").get();
                script = doc.select("body").select("script").first(); // Get the script part
                for (int i =0 ;i < 100; i++){
                    html = script.data();
                    if (html.contains("var transl_next_page='Следующая страница';")){
                        break;
                    }else script = script.nextElementSibling();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Не грузит страницу либо больше нечего грузить");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            TextView textView = (TextView) findViewById(R.id.text);
            StringBuilder secondBuffer = new StringBuilder(html);
            Log.i("Strign firdt: ", String.valueOf(secondBuffer.lastIndexOf("init")));
            Log.i("Strign false: ", String.valueOf(secondBuffer.lastIndexOf("false")));
            String second = html;
            Log.i("Strign firdt: ", secondBuffer.substring(secondBuffer.indexOf("init"), secondBuffer.lastIndexOf("false")));
            second =  secondBuffer.substring(secondBuffer.indexOf("init") + 6, secondBuffer.lastIndexOf("false") - 4);
            second = second.replace("[","");
            second = second.replace("]","");
            String[] test = second.split(",");

            // for(String tt: test) Log.i("Str: ", tt);

            String[] URL,URLhelp;
            URLhelp = new String[3];
            int kol = 0;
            int size = 0;
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
                    //   Log.i("URL", URL[size]);
                    size++;
                }
            }

        }
    }

    //поток для скачивания самих изображений
    public class parsImage extends AsyncTask<Void,Void,Void>{

        protected parsImage() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            //скачивания изображения
            InputStream inPut = null;
            try {
                inPut = new java.net.URL(urlPage.get(imageNumber)).openStream();
                //декод поток для загрузки изобр в Bitmap
                BitmapFactory.Options op = new BitmapFactory.Options();
                op.inPreferredConfig = Bitmap.Config.RGB_565; //без альфа-канала.
                op.inSampleSize = 1;
                image.add(BitmapFactory.decodeStream(inPut, null, op));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

        }
    }

}
