package com.example.nikita.progectmangaread.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassDescriptionMang;
import com.example.nikita.progectmangaread.Activity.TopManga;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 03.02.2016.
 * Фрагмент описания главное экграна манги
 * Жанры,переводится/закончено,описание,list с главами и т.п
 *
 */
public class fragmentDescriptionMang extends Fragment {
    private ClassDescriptionMang ClassDescriptionMang;
    private View v;
    private ProgressBar progress;
    private LinearLayout linearLayout,linearLayoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = null ;
        //System.out.println("!!!!!!!!!!!!!!!! I am create !!!!!!!!!!!!!!!!!!!!!");
        v = inflater.inflate(R.layout.description_screen_mang, null);
        progress = (ProgressBar) v.findViewById(R.id.loadingDescription);
        linearLayout = (LinearLayout) v.findViewById(R.id.linear_description_mang);
        GridLayout gridLayout = (GridLayout) v.findViewById(R.id.gridLayoutDescription);
        linearLayout.setVisibility(View.INVISIBLE);



        linearLayout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                EventBus.getDefault().post("Click");
            }
        });
        return v;
    }

    public void onEvent(ClassDescriptionMang event) {
        if (this.getArguments().getInt("num") == 0){
            if (v != null) {
                ClassDescriptionMang = event;
                TextView textView = (TextView) v.findViewById(R.id.textAuthor);
                textView.setText(event.getNameAuthor());
                textView = (TextView) v.findViewById(R.id.textRanck);
                textView.setText(event.getRank());
                textView = (TextView) v.findViewById(R.id.textGanres);
                textView.setText(event.getGenre());
                textView = (TextView) v.findViewById(R.id.textDescription);
                textView.setText(event.getDescription());
                textView = (TextView) v.findViewById(R.id.textVolumes);
                textView.setText(event.getToms());
                textView = (TextView) v.findViewById(R.id.textTranslate);
                textView.setText(event.getTranslate());
                textView = (TextView) v.findViewById(R.id.category);
                textView.setText(event.getCategory());

                ImageView imageView = (ImageView) v.findViewById(R.id.imageView2);
                if (TopManga.HEIGHT_WIND < TopManga.WIDTH_WIND){
                    imageView.setMinimumHeight(TopManga.HEIGHT_WIND / 3);
                    imageView.setMinimumWidth(TopManga.WIDTH_WIND / 4);
                }else {
                    imageView.setMinimumHeight(TopManga.HEIGHT_WIND / 4);
                    imageView.setMinimumWidth(TopManga.WIDTH_WIND / 3);
                }
                ImageLoader.getInstance().displayImage(event.getImg_url(), imageView);

                linearLayout.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);
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
