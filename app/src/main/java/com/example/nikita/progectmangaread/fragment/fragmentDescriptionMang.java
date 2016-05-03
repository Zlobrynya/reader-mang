package com.example.nikita.progectmangaread.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.classDescriptionMang;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 03.02.2016.
 * Фрагмент описания главное экграна манги
 * Жанры,переводится/закончено,описание,list с главами и т.п
 *
 */
public class fragmentDescriptionMang extends Fragment {
    private MainClassTop classDescription;
    private View v;
    private ProgressBar progress;
    private GridLayout gridLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //list = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = null ;
        System.out.println("!!!!!!!!!!!!!!!! I am create !!!!!!!!!!!!!!!!!!!!!");
        v = inflater.inflate(R.layout.glav_screen_mang, null);
        progress = (ProgressBar) v.findViewById(R.id.loadingDescription);
        gridLayout = (GridLayout) v.findViewById(R.id.gridLayoutDescription);
        return v ;
    }

    public void onEvent(classDescriptionMang event){

        if (this.getArguments().getInt("num") == 0){
            if (v != null) {
                TextView textView = (TextView) v.findViewById(R.id.textAuthor);
                textView.setText(event.getNameAuthor());
                textView = (TextView) v.findViewById(R.id.textRanck);
                textView.setText(event.getRank());
                textView = (TextView) v.findViewById(R.id.textDescription);
                textView.setText(event.getDescription());
                textView = (TextView) v.findViewById(R.id.textGanres);
                textView.setText(event.getGenre());
                textView = (TextView) v.findViewById(R.id.textVolumes);
                textView.setText(event.getToms());
                textView = (TextView) v.findViewById(R.id.textTranslate);
                textView.setText(event.getTranslate());
                textView = (TextView) v.findViewById(R.id.category);
                textView.setText(event.getCategory());

                ImageView imageView = (ImageView) v.findViewById(R.id.imageView2);
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                imageView.setMinimumHeight(displaymetrics.heightPixels / 4);
                imageView.setMinimumWidth(displaymetrics.widthPixels / 3);
                imageView.setImageBitmap(event.getImgMang());
                ImageLoader.getInstance().displayImage(event.getImg_url(), imageView);

                progress.setVisibility(View.INVISIBLE);
                gridLayout.setVisibility(View.VISIBLE);
            }
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

    //фабричный метод для ViewPage
    public static fragmentDescriptionMang newInstance(int page) {
        fragmentDescriptionMang fragment = new fragmentDescriptionMang();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }
}
