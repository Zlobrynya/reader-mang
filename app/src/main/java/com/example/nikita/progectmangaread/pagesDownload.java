package com.example.nikita.progectmangaread;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Nikita on 07.03.2016.
 *
 * Сделать работу с изображение(приближение отдаление и т.п.)
 * Разобраться с новой библиотекой UIL.
 *
 */
public class pagesDownload extends Activity {
    public ArrayList<String> urlPage;
    public ArrayList<Bitmap> image;
    public int imageNumber;
    private ImageAdapter adapter;
    String URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pageview_image);
        urlPage = new ArrayList<>();
        image = new ArrayList<>();
        ViewPager pager = (ViewPager) findViewById(R.id.pagerImage);
        adapter = new ImageAdapter(this);
        pager.setAdapter(adapter);


        Intent intent = getIntent();
        URL = intent.getStringExtra("URL");

        ParsURLPage par = new ParsURLPage(URL);
        par.execute();
    }

    class ImageAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private DisplayImageOptions options;
        protected ImageLoader imageLoader;

        ImageAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true)
                    .displayer(new FadeInBitmapDisplayer(300))
                    .build();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return urlPage.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.layout_fullscreen_image, view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.imgDisplay);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

            imageLoader.getInstance().displayImage(urlPage.get(position), imageView, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    spinner.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    String message = null;
                    switch (failReason.getType()) {
                        case IO_ERROR:
                            message = "Input/Output error";
                            break;
                        case DECODING_ERROR:
                            message = "Image can't be decoded";
                            break;
                        case NETWORK_DENIED:
                            message = "Downloads are denied";
                            break;
                        case OUT_OF_MEMORY:
                            message = "Out Of Memory error";
                            break;
                        case UNKNOWN:
                            message = "Unknown error";
                            break;
                    }
                    Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();

                    spinner.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    spinner.setVisibility(View.GONE);
                }
            });

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
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
                if (doc == null) doc = Jsoup.connect("http://readmanga.me"+URL).get();
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
            adapter.notifyDataSetChanged();
        }
    }
}
