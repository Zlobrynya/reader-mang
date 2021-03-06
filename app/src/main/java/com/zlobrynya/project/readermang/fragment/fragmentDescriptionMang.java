package com.zlobrynya.project.readermang.fragment;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.classPMR.ClassDescriptionMang;
import com.zlobrynya.project.readermang.Activity.TopManga;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Nikita on 03.02.2016.
 * Фрагмент описания главное экграна манги
 * Жанры,переводится/закончено,описание,list с главами и т.п
 *
 */
public class fragmentDescriptionMang extends Fragment {
    private View v;
    private ProgressBar progress;
    private ConstraintLayout constraintLayout;
    private final String PROBLEM = "ProblemTime";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(PROBLEM, "Create fragmentDescriptionMang");
        EventBus.getDefault().register(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = null ;
        //System.out.println("!!!!!!!!!!!!!!!! I am create !!!!!!!!!!!!!!!!!!!!!");
        v = inflater.inflate(R.layout.description_screen_mang, null);
        progress = (ProgressBar) v.findViewById(R.id.loadingDescription);
        constraintLayout = (ConstraintLayout) v.findViewById(R.id.constraint_description_mang);
        constraintLayout.setVisibility(View.INVISIBLE);
        Log.i(PROBLEM, "Start fragmentDescriptionMang");

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EventBus.getDefault().post("Click");
            }
        });
        Log.i(PROBLEM, "End Start fragmentDescriptionMang");
        return v;
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void onEvent(ClassDescriptionMang event) {
        if (this.getArguments().getInt("num") == 1){
            if (v != null) {
               // com.zlobrynya.nikita.readermang.classPMR.ClassDescriptionMang classDescriptionMang = event;
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
                textView = (TextView) v.findViewById(R.id.textCategory);
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

                if (event.isDownload()){
                    Button button = (Button) v.findViewById(R.id.open_browser);
                    button.setVisibility(View.GONE);
                }

                constraintLayout.setVisibility(View.VISIBLE);
                progress.setVisibility(View.INVISIBLE);
            }
        }
        //Отмена евента, что бы он дальше не шел
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
