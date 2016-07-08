package com.example.nikita.progectmangaread.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterList;
import com.example.nikita.progectmangaread.DataBasePMR.classDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseListMang;
import com.example.nikita.progectmangaread.classPMR.classDescriptionMang;
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
    private String nameMang,imgURL;
    private classDataBaseViewedHead classDataBaseViewedHead;
    private ClassDataBaseListMang ClassDataBaseListMang;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        myAdap = new AdapterList(getActivity(), R.layout.layout_for_list_view, list);
        EventBus.getDefault().register(this);
        setRetainInstance(true);
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
                classForList classForList1 = list.get(position);
                classForList1.setCheck(true);
                classForList1.setNumberChapter(position);
                //classForList1.setURL_chapter(classForList1.getURL_chapter());
                list.set(position, classForList1);
                myAdap.notifyDataSetChanged();
                classDataBaseViewedHead.editBaseDate(nameMang, String.valueOf(position));
                //      classDataBaseViewedHead.setData(nameMang, imgURL, DataBaseViewedHead.URL_IMG);

                // classDataBaseViewedHead.editLastChapter(nameMang,classForList1.getURL_chapter());
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
        classDataBaseViewedHead.editBaseDate(nameMang, String.valueOf(event));
        EventBus.getDefault().post(classForList1);
    }

    public void onEvent(java.lang.String event){
        if (event.contains("notebook")){
            int notebook = Integer.parseInt(classDataBaseViewedHead.getDataFromDataBase(nameMang, classDataBaseViewedHead.NOTEBOOK));
            if (notebook == 0){
                classDataBaseViewedHead.setData(nameMang, String.valueOf(1), classDataBaseViewedHead.NOTEBOOK);
                String lastChapter = classDataBaseViewedHead.getDataFromDataBase(nameMang,classDataBaseViewedHead.LAST_CHAPTER);
                if (lastChapter.contains("null")){
                 //   classDataBaseViewedHead.setData(nameMang, imgURL, DataBaseViewedHead.URL_IMG);
                    classDataBaseViewedHead.editLastChapter(nameMang, list.get(list.size() - 1).getURL_chapter());
                    classDataBaseViewedHead.setData(nameMang, list.get(list.size()-1).getName_chapter(),classDataBaseViewedHead.NAME_LAST_CHAPTER);
                }
            }else {
                classDataBaseViewedHead.setData(nameMang, String.valueOf(0),classDataBaseViewedHead.NOTEBOOK);
            }

        }
    }

    //тут посылка с DescriptionMang, что надо бы добавить в list и обновить адаптер
    public void onEvent(classTransportForList event){
        if (!event.getName().isEmpty()){
            ClassDataBaseListMang = new ClassDataBaseListMang(getActivity(),event.getMainClassTop().getURL_site());
            if (!ClassDataBaseListMang.thereIsInTheDatabase(event.getMainClassTop().getName_characher())){
                ClassDataBaseListMang.addBasaData(event.getMainClassTop(),-1);
            }
            ArrayList<classForList> arrayList = event.getClassForList();
            for (classForList b: arrayList){
                if (!b.getURL_chapter().contains("?mature=1")){
                    b.setURL_chapter(b.getURL_chapter()+"?mature=1");
                    list.add(b);
                }
            }
            nameMang = event.getName();
            classDataBaseViewedHead = new classDataBaseViewedHead(getActivity());
            if (classDataBaseViewedHead.addBasaData(event.getName())){
                String strings;
                strings = classDataBaseViewedHead.getDataFromDataBase(event.getName(),classDataBaseViewedHead.VIEWED_HEAD);
                if (!strings.contains("null")){
                    String string[] = strings.split(",");
                    for (String aString : string) {
                        classForList classForList1 = list.get(Integer.parseInt(aString));
                        classForList1.setCheck(true);
                        list.set(Integer.parseInt(aString), classForList1);
                    }
                }else{
                    //Нахрена это надо?
                 //   classDataBaseViewedHead.setData(event.getName(), String.valueOf(1), classDataBaseViewedHead.LAST_PAGE);
                 //   classDataBaseViewedHead.editLastChapter(event.getName(),list.get(list.size()-1).getURL_chapter());
                }
            }
            myAdap.notifyDataSetChanged();
        }
    }

    public void onEvent(classDescriptionMang event) {
        imgURL = event.getImg_url();
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
        //Закрываем базу данных
        if (classDataBaseViewedHead != null)
            ClassDataBaseListMang.closeDataBase();
        if (classDataBaseViewedHead != null)
            classDataBaseViewedHead.closeDataBase();
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
