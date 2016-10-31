package com.example.nikita.progectmangaread.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.nikita.progectmangaread.Activity.DescriptionMang;
import com.example.nikita.progectmangaread.AdapterPMR.AdapterOtherMang;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassMainTop;
import com.example.nikita.progectmangaread.classPMR.ClassOtherMang;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Nikita on 08.10.2016.
 *
 * Для отображения связных и похожих манг.
 *
 */

public class fragmentOtherMang extends Fragment {
    private AdapterOtherMang adapterOtherMang;
    private ArrayList<ClassOtherMang> list;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        adapterOtherMang = new AdapterOtherMang(getActivity(), R.layout.other_manga, list);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sticky_list, null);
        StickyListHeadersListView listView = (StickyListHeadersListView) v.findViewById(R.id.listRecentlyRead);
        listView.setAdapter(adapterOtherMang);
        textView= (TextView) v.findViewById(R.id.text_sticky_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //запуск другого активити DescriptionMang
                Intent intent = new Intent(getActivity(), DescriptionMang.class);
                ClassOtherMang mainTop = list.get(position);
                if (mainTop != null) {
                    intent.putExtra("URL_ch", mainTop.getURLchapter());
                    intent.putExtra("Url_img", mainTop.getURL_img());
                    intent.putExtra("Name_ch", mainTop.getNameMang());
                    intent.putExtra("Url_site", mainTop.getUrlSite());
                    startActivity(intent);
                    Log.v("long clicked", "pos: " + position);
                }
            }
        });

        return v;
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

    public void onEvent(ArrayList<ClassOtherMang> event) {
        list.addAll(event);
        adapterOtherMang.notifyDataSetChanged();
        if (list.isEmpty()){
            textView.setText(R.string.not_other_mang);
            textView.setVisibility(View.VISIBLE);
        }
     //   EventBus.getDefault().cancelEventDelivery(event) ;
    }

    //фабричный метод для ViewPage
    public static fragmentOtherMang newInstance() {
        fragmentOtherMang fragment = new fragmentOtherMang();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
