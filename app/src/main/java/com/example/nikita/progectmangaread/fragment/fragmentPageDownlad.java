package com.example.nikita.progectmangaread.fragment;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.cacheImage.cacheFile;
import com.example.nikita.progectmangaread.decode.MyImageDecoder;
import com.example.nikita.progectmangaread.decode.MyImageRegionDecoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 10.03.2016.
 */
public class fragmentPageDownlad extends Fragment {
    private int number;
    private cacheFile file;
    SubsamplingScaleImageView image;
    ProgressBar progress;
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
        Log.i("Destroy:", String.valueOf(getArguments().get("imageId")));
     //   file.clearCache();
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    SubsamplingScaleImageView.OnImageEventListener d = new SubsamplingScaleImageView.OnImageEventListener() {
        @Override
        public void onReady() {
           // Log.i("heR", String.valueOf(image.getSHeight()));
        }

        @Override
        public void onImageLoaded() {
            Log.i("heL", String.valueOf(image.getSHeight()));
            if (image.getSHeight() < 3000){
                image.setDoubleTapZoomDpi(80);
            }else {

            }
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
        final View v = inflater.inflate(R.layout.layout_fullscreen_image, null);

        image = (SubsamplingScaleImageView)v.findViewById(R.id.imageView);
        image.setBitmapDecoderClass(MyImageDecoder.class);
        image.setRegionDecoderClass(MyImageRegionDecoder.class);
        progress = (ProgressBar) v.findViewById(R.id.loading);
        image.setOnImageEventListener(d);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("CkickImage");
            }
        });
        number = getArguments().getInt("imageId");
        final String url = getArguments().getString("String");
        progress.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);
      //  image.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        image.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        image.setMinimumDpi(50);
        AsyncTaskLisen as = new AsyncTaskLisen() {
            @Override
            public void onEnd() {
                try {
                    image.setImage(ImageSource.uri(file.getFile(String.valueOf(number))));
                    image.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                } catch (FileNotFoundException e) {

                }
            }

            @Override
            public void onEnd(InputStream is) {
            }
        };

        file = new cacheFile(getContext().getCacheDir(),"pageCache",as);
        file.loadAndCache(url, number);
        return v;
    }
}
