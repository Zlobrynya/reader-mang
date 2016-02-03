package com.example.nikita.progectmangaread.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterList;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.classForList;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 03.02.2016.
 */
public class fragmentDescriptionList extends Fragment {
    View v;
    ArrayList<classForList> list;
    public AdapterList myAdap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        myAdap = new AdapterList(getActivity(), R.layout.layout_for_list_view, list);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_heads, null);
        ListView gr = (ListView) v.findViewById(R.id.listView);
        gr.setAdapter(myAdap);
        classForList classForList = new classForList();
        classForList.setName_chapter("GG");
        for (int i = 0 ; i < 150; i++){
            list.add(classForList);
        }
        return v ;
    }


    public void onEvent(ArrayList<String> event){

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
    public static fragmentDescriptionList newInstance(int page) {
        fragmentDescriptionList fragment = new fragmentDescriptionList();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }
}
