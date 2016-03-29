package com.example.nikita.progectmangaread.fragment;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterList;
import com.example.nikita.progectmangaread.DataBasePMR.DataBaseViewedHead;
import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.classForList;
import com.example.nikita.progectmangaread.classPMR.classTransportForList;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 03.02.2016.
 *
 */
public class fragmentDescriptionList extends Fragment {
    View v;
    ArrayList<classForList> list;
    public AdapterList myAdap;
    private ListView gr;
    private String nameMang;
    private classDataBaseViewedHead classDataBaseViewedHead;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        myAdap = new AdapterList(getActivity(), R.layout.layout_for_list_view, list);
        EventBus.getDefault().register(this);
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
                classForList1.setNumberChapter(position);
                classForList1.setURL_chapter(classForList1.getURL_chapter() + "?mature=1");
                list.set(position, classForList1);
                myAdap.notifyDataSetChanged();
                classDataBaseViewedHead.editBaseDate(nameMang,String.valueOf(position));
                //+"?mature=1"
                Log.i("post", classForList1.getURL_chapter() + "?mature=1");
                EventBus.getDefault().post(classForList1);
            }
        });

        return v;
    }
    //"Посылка" с fragmentPageDowland, что надо переключить главу
    public void onEvent(java.lang.Integer event){
        classForList classForList1 = list.get(event);
        classForList1.setNumberChapter(event);
        if (!classForList1.getCheck())
            classForList1.setCheck(true);
        list.set(event, classForList1);
        myAdap.notifyDataSetChanged();
        classDataBaseViewedHead.editBaseDate(nameMang,String.valueOf(event));
        EventBus.getDefault().post(classForList1);
    }
    //тут посылка с DescriptionMang, что надо бы добавить в list и обновить адаптер
    public void onEvent(classTransportForList event){
        ArrayList<classForList> arrayList = event.getClassForList();
        for (classForList b: arrayList){
            list.add(b);
        }
        nameMang = event.getName();
        classDataBaseViewedHead = new classDataBaseViewedHead(getActivity());
        if (classDataBaseViewedHead.addBasaData(event.getName())){
            String strings;
            strings = classDataBaseViewedHead.getDataFromDataBase(event.getName(),DataBaseViewedHead.VIEWED_HEAD);
            if (!strings.contains("null")){
                String string[] = strings.split(",");
                for (String aString : string) {
                    classForList classForList1 = list.get(Integer.parseInt(aString));
                    classForList1.setCheck(true);
                    list.set(Integer.parseInt(aString), classForList1);
                }
            }
        }
        myAdap.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
