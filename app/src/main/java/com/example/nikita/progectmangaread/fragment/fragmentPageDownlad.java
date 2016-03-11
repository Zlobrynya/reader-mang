package com.example.nikita.progectmangaread.fragment;

import android.app.ActionBar;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
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
import com.example.nikita.progectmangaread.classPMR.classTouchImageView;

import java.io.IOException;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 10.03.2016.
 */
public class fragmentPageDownlad extends Fragment {

    public static fragmentPageDownlad getInstance(int imageId,String url) {
        final fragmentPageDownlad instance = new fragmentPageDownlad();
        final Bundle params = new Bundle();
        params.putInt("imageId", imageId);
        params.putString("String", url);
        instance.setArguments(params);

        return instance;
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

        //установка изображение на весь экран
        Matrix m = image.getImageMatrix();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        RectF drawableRect = new RectF(0, 0, displaymetrics.widthPixels, displaymetrics.heightPixels);
        RectF viewRect = new RectF(0, 0, displaymetrics.widthPixels, displaymetrics.heightPixels);
        m.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.FILL);
        image.setImageMatrix(m);

        final String url = getArguments().getString("String");
        final ProgressBar progress = (ProgressBar) v.findViewById(R.id.loading);

        progress.setVisibility(View.VISIBLE);
        image.setVisibility(View.GONE);

        AsyncTaskLisen addImg = new AsyncTaskLisen() {
            @Override
            public void onEnd() {
            }

            @Override
            public void onEnd(InputStream is) {
                TileBitmapDrawable.attachTileBitmapDrawable(image, is, null, new TileBitmapDrawable.OnInitializeListener() {
                    @Override
                    public void onStartInitialization() {
                    }

                    @Override
                    public void onEndInitialization() {
                        progress.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                    }
                });

            }
        };
        ParsPage pr = new ParsPage(addImg,url);
        pr.execute();
        return v;
    }

    class ParsPage extends AsyncTask<Void,Void,Void> {
        private AsyncTaskLisen asyncTaskLisen;
        private String url;
        private InputStream is;
        protected ParsPage(AsyncTaskLisen addImg,String url){
            asyncTaskLisen = addImg;
            this.url = url;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                assert url != null;
                is = new java.net.URL(url).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            asyncTaskLisen.onEnd(is);
        }

    }
}
