package com.example.nikita.progectmangaread;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 03.02.2016.
 * Фрагмент описания главное экграна манги
 * Жанры,переводится/закончено,описание,list с главами и т.п
 *
 */
public class fragmentDescriptionMang extends Fragment {
    MainClassTop classDescription;
    View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //list = new ArrayList<>();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = null ;
        System.out.println("!!!!!!!!!!!!!!!! I am create !!!!!!!!!!!!!!!!!!!!!");
        switch (this.getArguments().getInt("num")){
            case 0:
                v = inflater.inflate(R.layout.glav_screen_mang, null);
                break;
            case 1:
                //тут заменить на layout со списком
                v = inflater.inflate(R.layout.activity_main, null);
                break;
            default:
                v = inflater.inflate(R.layout.fragment, null);
        }
        return v ;
    }

    public void onEvent(classDescriptionMang event){
        System.out.println("!!!!!!!!!!!!!!!! Event !!!!!!!!!!!!!!!!!!!!!");
        if (this.getArguments().getInt("num") == 0){
            TextView textView = (TextView) v.findViewById(R.id.textAuthor);
            textView.setText(event.getNameAuthor());
            textView = (TextView) v.findViewById(R.id.textDescription);
            textView.setText(event.getDescription());
            textView = (TextView) v.findViewById(R.id.textGanres);
            textView.setText(event.getGenre());
            textView = (TextView) v.findViewById(R.id.textVolumes);
            textView.setText(event.getToms());
            textView = (TextView) v.findViewById(R.id.textRanck);
            textView.setText(event.getRank());
            ImageView img = (ImageView) v.findViewById(R.id.imageView2);
            img.setImageBitmap(event.getImgMang());
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
