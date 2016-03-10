package com.example.nikita.progectmangaread.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterList;
import com.example.nikita.progectmangaread.DescriptionMang;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.classForList;
import com.example.nikita.progectmangaread.pagesDownload;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 03.02.2016.
 */
public class fragmentDescriptionList extends Fragment {
    View v;
    ArrayList<classForList> list;
    public AdapterList myAdap;
    private ListView gr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        myAdap = new AdapterList(getActivity(), R.layout.layout_for_list_view, list);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_heads, null);
        gr = (ListView) v.findViewById(R.id.listView);
        gr.setAdapter(myAdap);
        final classForList classForList = new classForList();
        classForList.setName_chapter("GG");

        gr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Разобраться с багом при check
                classForList classForList1 = list.get(position);
                classForList1.setCheck(true);
                list.set(position, classForList1);
                myAdap.notifyDataSetChanged();
                //+"?mature=1"
                String urlPic = classForList1.getURL_chapter()+"?mature=1";
                Log.i("post",urlPic);
                EventBus.getDefault().post(urlPic);
            }
        });

        return v ;
    }


    public void onEvent(classForList event){
        myAdap.add(event);
        myAdap.notifyDataSetChanged();
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
