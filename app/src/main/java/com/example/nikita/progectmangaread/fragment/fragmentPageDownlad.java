package com.example.nikita.progectmangaread.fragment;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.nikita.progectmangaread.Activity.PagesDownload;
import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.cacheImage.CacheFile;
import com.example.nikita.progectmangaread.decode.MyImageDecoder;
import com.example.nikita.progectmangaread.decode.MyImageRegionDecoder;
import com.example.nikita.progectmangaread.Activity.TopManga;

import java.io.File;
import java.io.FileNotFoundException;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 10.03.2016.
 */
public class fragmentPageDownlad extends Fragment{
    private int number;
    private CacheFile file;
    private SubsamplingScaleImageView image;
    private ProgressBar progress;
    private final String strLog = "fragmentPageDownload";

    public static fragmentPageDownlad getInstance(int imageId,String url) {
        final fragmentPageDownlad instance = new fragmentPageDownlad();
        final Bundle params = new Bundle();
        params.putInt("imageId", imageId);
        params.putString("String", url);
        instance.setArguments(params);
        return instance;
    }

    @Override
    public void onDestroy() {
        Log.i("fragmentPageDownload:", "Destroy: " + String.valueOf(getArguments().get("imageId")));
        if (file != null)
            file.stopAsyncTask();
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("fragmentPageDownload:", "Create: " + String.valueOf(getArguments().get("imageId")));
        super.onCreate(savedInstanceState);
    }

    SubsamplingScaleImageView.OnImageEventListener d = new SubsamplingScaleImageView.OnImageEventListener() {
        @Override
        public void onReady() {
            Log.i("Image", "ready");
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onImageLoaded() {
            if (image.getSHeight() > 3000){
                image.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
            } else {
                image.setDoubleTapZoomDpi(150);
            }

            Log.i("Image","Load");
            progress.setVisibility(View.GONE);
        }

        @Override
        public void onPreviewLoadError(Exception e) {

        }

        @Override
        public void onImageLoadError(Exception e) {

        }

        @Override
        public void onTileLoadError(Exception e) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fullscreen_image, null);

        image = (SubsamplingScaleImageView)v.findViewById(R.id.imageView);
        progress = (ProgressBar) v.findViewById(R.id.loading);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progress.getLayoutParams();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            params.setMargins(0, TopManga.HEIGHT_WIND / 2, 0, 0);
        }else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            params.setMargins(0, TopManga.WIDTH_WIND / 2, 0, 0);
        }
        progress.setLayoutParams(params);
        image.setBitmapDecoderClass(MyImageDecoder.class);
        image.setRegionDecoderClass(MyImageRegionDecoder.class);
        image.setOnImageEventListener(d);
      /*  image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("CkickImage");
            }
        });*/
        number = getArguments().getInt("imageId");
        final String url = getArguments().getString("String");
        //Настройки прогресс бара
        progress.setVisibility(View.VISIBLE);
        progress.setIndeterminate(false);
        progress.setMax(110);

        image.setVisibility(View.GONE);
        //установка на сколько приближается при двойном тапе

        image.setMinimumDpi(60);
        file = new CacheFile(new File(PagesDownload.pathDir), PagesDownload.nameDirectory ,null);
        if (file.checkFile(String.valueOf(number)))
            showImageView();
     /*   if (PagesDownload.threadManager.isImageSave(number)){
            showImageView();
        }*/

        if (!PagesDownload.nameDirectory.contains("pageCache")) {
            showImageView();
        }
        return v;
    }

    private void showImageView(){
        try {
            if (image != null) {
                if (!image.isReady()){
                    progress.setProgress(100);
                    image.setImage(ImageSource.uri(file.getFile(String.valueOf(number))));
                    image.setVisibility(View.VISIBLE);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
    public void onResume() {
        showImageView();
        super.onResume();
    }


   /* public void onEvent(Byte event) {
        if (event == number){
            showImageView();
        }
    }*/

        // Принимает евенты о скачивании от CacheFile и ThreadManager
    // разделение в строке идет: / от CacheFile, если не чего делить то выводим на экран изображение
    public void onEvent(String event){
        String[] strings = event.split("/");
        if (strings.length == 2){
            if (strings[0].contains(String.valueOf(number))){
                if (strings[1].contains("reload")){
                    image.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(0);
                }else if (Integer.parseInt(strings[1]) == 100){
                    showImageView();
                }progress.setProgress(Integer.parseInt(strings[1]));

            }
        }
    }

}
