package com.example.nikita.progectmangaread.fragment;

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
import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.cacheImage.CacheFile;
import com.example.nikita.progectmangaread.decode.MyImageDecoder;
import com.example.nikita.progectmangaread.decode.MyImageRegionDecoder;
import com.example.nikita.progectmangaread.Activity.TopManga;

import java.io.FileNotFoundException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 10.03.2016.
 */
public class fragmentPageDownlad extends Fragment {
    private int number;
    private CacheFile file;
    private SubsamplingScaleImageView image;
    private ProgressBar progress;

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
            Log.i("Image", "ready");
          //  progress.setVisibility(View.GONE);
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
        params.setMargins(0, TopManga.HEIGHT_WIND / 2, 0, 0);
        progress.setLayoutParams(params);

        image.setBitmapDecoderClass(MyImageDecoder.class);
        image.setRegionDecoderClass(MyImageRegionDecoder.class);
        image.setOnImageEventListener(d);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("CkickImage");
            }
        });
        number = getArguments().getInt("imageId");
        final String url = getArguments().getString("String");
        //Настройки прогресс бара
        progress.setVisibility(View.VISIBLE);
        progress.setIndeterminate(false);
        progress.setMax(110);

        image.setVisibility(View.GONE);
        //установка на сколько приближается при двойном тапе

        image.setMinimumDpi(60);
        AsyncTaskLisen as = new AsyncTaskLisen() {
            @Override
            public void onEnd() {
                try {
                    image.setImage(ImageSource.uri(file.getFile(String.valueOf(number))));
                    image.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    file.loadAndCache(url, number);
                }
            }
            @Override
            public void onEnd(InputStream is) {
            }
        };

        file = new CacheFile(getContext().getCacheDir(),"pageCache",as,progress);
        file.loadAndCache(url, number);
        return v;
    }
}
