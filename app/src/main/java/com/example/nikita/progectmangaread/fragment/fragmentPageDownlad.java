package com.example.nikita.progectmangaread.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.diegocarloslima.byakugallery.lib.TileBitmapDrawable;
import com.diegocarloslima.byakugallery.lib.TouchImageView;
import com.example.nikita.progectmangaread.AsyncTaskLisen;
import com.example.nikita.progectmangaread.R;

import java.io.IOException;
import java.io.InputStream;

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

        final TouchImageView image = (TouchImageView) v.findViewById(R.id.my_image);
        final int imageId = getArguments().getInt("imageId");
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
