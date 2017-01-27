package com.zlobrynya.project.readermang.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.zlobrynya.project.readermang.Activity.ShowPages;
import com.zlobrynya.project.readermang.Activity.TopManga;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.cacheImage.CacheFile;
import com.zlobrynya.project.readermang.decode.MyImageDecoder;
import com.zlobrynya.project.readermang.decode.MyImageRegionDecoder;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Nikita on 10.03.2016.
 */
public class fragmentPageDownlad extends Fragment {
    private int idPage;
    private CacheFile file;
    private SubsamplingScaleImageView image;
    private ProgressBar progress;
    private final String strLog = "fragmentPageDownload";
    private TextView textView;
    private SharedPreferences.Editor editor;
    private boolean saveZoom;

    public static fragmentPageDownlad getInstance(int imageId, String url) {
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
            textView.setVisibility(View.GONE);
        }

        @Override
        public void onImageLoaded() {
            if (image.getSHeight() > 3000) {
                image.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
            } else {
                image.setDoubleTapZoomScale(2);
            }

            Log.i("Image", "Load");
            progress.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }

        @Override
        public void onPreviewLoadError(Exception e) {

        }

        @Override
        public void onImageLoadError(Exception e) {
            EventBus.getDefault().post("FailGetImg/" + idPage);
        }

        @Override
        public void onTileLoadError(Exception e) {

        }

        @Override
        public void onPreviewReleased() {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fullscreen_image, null);

        SharedPreferences mSettings = getActivity().getSharedPreferences(TopManga.APP_SETTINGS, Context.MODE_PRIVATE);

        saveZoom = mSettings.getBoolean(TopManga.APP_SETTINGS_SAVE_ZOOM, true);

        image = (SubsamplingScaleImageView) v.findViewById(R.id.imageView);
        progress = (ProgressBar) v.findViewById(R.id.loading);
        textView = (TextView) v.findViewById(R.id.text_view_fullscreen);

        image.setBitmapDecoderClass(MyImageDecoder.class);
        image.setRegionDecoderClass(MyImageRegionDecoder.class);
        image.setOnImageEventListener(d);

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (saveZoom) {
                    Helper helper = new Helper();
                    helper.id = idPage;
                    helper.scale = image.getScale();
                    EventBus.getDefault().post(helper);
                    if (image.getScale() > 2){
                        image.setDoubleTapZoomScale(image.getScale());
                    }
                }
                return image.onTouchEvent(motionEvent);
            }
        });

        // image.setDebug(true);
      /*  image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("CkickImage");
            }
        });*/
        idPage = getArguments().getInt("imageId");

        image.setVisibility(View.GONE);
        //установка на сколько приближается при двойном тапе
        // image.setMinimumDpi(60);
        file = new CacheFile(new File(ShowPages.pathDir), ShowPages.nameDirectory, null);
        if (file.checkFile(String.valueOf(idPage)))
            showImageView();

        if (!ShowPages.nameDirectory.contains("pageCache")) {
            showImageView();
        }
        return v;
    }


    private void showImageView() {
        try {
            if (image != null) {
                if (file.checkFile(String.valueOf(idPage))) {
                    if (!image.isReady() || !image.isShown()) {
                        Log.i("ImageStart", "ImageStart " + idPage);
                        image.destroyDrawingCache();
                        image.setImage(ImageSource.uri(file.getFile(String.valueOf(idPage))));
                        image.setVisibility(View.VISIBLE);
                        // progress.setVisibility(View.GONE);
                      /*  if (saveZoom) {
                            SharedPreferences mSettings = getActivity().getSharedPreferences(TopManga.APP_SETTINGS, Context.MODE_PRIVATE);
                            float scale = mSettings.getFloat(TopManga.APP_SETTINGS_IMAGE_SCALE, 150);
                            image.setScaleAndCenter(scale, new PointF(10, image.getWidth() - 50));
                        }*/
                    }
                }
            }
        } catch (EOFException e) {
            EventBus.getDefault().post("FailGetImg/" + idPage);
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
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String event) {
        String[] strings = event.split("/");
        if (strings.length == 2) {
            if (strings[0].contains(String.valueOf(idPage))) {
                if (strings[1].contains("reload")) {
                    image.setVisibility(View.GONE);
                    progress.setVisibility(View.VISIBLE);
                } else if (strings[1].contains("Start")) {
                    showImageView();
                }
                if (strings[1].contains("reloadDown"))
                    showImageView();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Helper event) {
        if (event.id != idPage){
            if (saveZoom) {
                if (event.scale == 0)
                    event.scale = 150;
                image.setScaleAndCenter(event.scale, new PointF(image.getWidth(),10));
            }
        }
    }

    private class Helper{
        public int id;
        public float scale;
    }

}