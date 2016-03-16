package com.example.nikita.progectmangaread.fragment;

import android.app.ActionBar;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import java.io.FileInputStream;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.diegocarloslima.byakugallery.lib.TileBitmapDrawable;
import com.diegocarloslima.byakugallery.lib.TouchImageView;
import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.cacheImage.cacheFile;
import com.example.nikita.progectmangaread.classPMR.classTouchImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 10.03.2016.
 */
public class fragmentPageDownlad extends Fragment {
    private int number;
    private cacheFile file;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.layout_fullscreen_image, null);
        final classTouchImageView image = (classTouchImageView) v.findViewById(R.id.my_image);
        //слушатель для клика
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("CkickImage");
            }
        });
        number = getArguments().getInt("imageId");
        final String url = getArguments().getString("String");
        final ProgressBar progress = (ProgressBar) v.findViewById(R.id.loading);
        progress.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);

        AsyncTaskLisen as = new AsyncTaskLisen() {
            @Override
            public void onEnd() {
                try {
                    TileBitmapDrawable.attachTileBitmapDrawable(image, file.getFile(String.valueOf(number)), null, new TileBitmapDrawable.OnInitializeListener() {
                        @Override
                        public void onStartInitialization() {
                        }

                        @Override
                        public void onEndInitialization() {
                            progress.setVisibility(View.GONE);
                            image.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (FileNotFoundException e) {

                }
            }

            @Override
            public void onEnd(InputStream is) {
            }
        };

        file = new cacheFile(getContext().getCacheDir(),"pageCache",as);
        file.loadAndCache(url, number);
       // file.clearCache();

        return v;
    }
}
