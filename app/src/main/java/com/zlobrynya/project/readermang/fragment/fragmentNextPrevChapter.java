package com.zlobrynya.project.readermang.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zlobrynya.project.readermang.R;

/**
 * Created by Nikita on 16.03.2016.
 * Этот фрагмент как "закрывалка" для определение что страницы кончились и
 * нужно пролистать на след страницу.
 */
public class fragmentNextPrevChapter extends Fragment {

    public static fragmentNextPrevChapter getInstance(int imageId,String url) {
        final fragmentNextPrevChapter instance = new fragmentNextPrevChapter();
        final Bundle params = new Bundle();
        params.putString("String", url);
        instance.setArguments(params);

        return instance;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.next_previous_chapter, null);
        final String url = getArguments().getString("String");
        TextView textView = (TextView) v.findViewById(R.id.textNextPrev);
        textView.setText(url);
        return v;
    }


}
