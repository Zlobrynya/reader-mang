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
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseListMang;
import com.example.nikita.progectmangaread.classPMR.ClassDescriptionMang;
import com.example.nikita.progectmangaread.classPMR.ClassForList;
import com.example.nikita.progectmangaread.classPMR.ClassTransportForList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 03.02.2016.
 *
 */
public class fragmentDescriptionList extends Fragment {
    private View v;
    private ArrayList<ClassForList> list;
    private AdapterList myAdap;
    private ListView listView;
    private String nameMang,imgURL;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private ClassDataBaseListMang ClassDataBaseListMang;
    private boolean readDownloaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        readDownloaded = false;
        myAdap = new AdapterList(getActivity(), R.layout.list_view_checkbox, list);
        EventBus.getDefault().register(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_heads, null);
        listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(myAdap);
        final ClassForList classForList = new ClassForList();
        classForList.setName_chapter("GG");


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ClassForList classForList1 = list.get(position);
                classForList1.setCheck(true);
                classForList1.setNumberChapter(position);
                list.set(position, classForList1);
                myAdap.notifyDataSetChanged();

                if (!readDownloaded){
                    classDataBaseViewedHead.editBaseDate(nameMang, String.valueOf(position));
                    //Получаем дату когда тыкнули главу и загрузили в бд
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = df.format(c.getTime());
                    classDataBaseViewedHead.setData(nameMang, formattedDate, ClassDataBaseViewedHead.DATA);
                    Log.i("Data", formattedDate);
                }else {
                    classForList1.setDownload(true);
                }
                EventBus.getDefault().post(classForList1);
            }
        });

        return v;
    }
    //"Посылка" с fragmentPageDowland, что надо переключить главу
    public void onEvent(java.lang.Integer event){
        ClassForList classForList1 = list.get(event);
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
                    classDataBaseViewedHead.editLastChapter(nameMang, list.get(list.size() - 1).getURL_chapter());
                    classDataBaseViewedHead.setData(nameMang, list.get(list.size() - 1).getName_chapter(), classDataBaseViewedHead.NAME_LAST_CHAPTER);
                }
            }else {
                classDataBaseViewedHead.setData(nameMang, String.valueOf(0), classDataBaseViewedHead.NOTEBOOK);
            }

        }
    }

    //тут посылка с DescriptionMang, что надо бы добавить в list и обновить адаптер
    public void onEvent(ClassTransportForList event){
        if (!event.getName().isEmpty()){
            list.clear();
            nameMang = event.getName();
            //Проверка откуда пришел евент
            if (event.getMainClassTop() != null){
                ClassDataBaseListMang = new ClassDataBaseListMang(getActivity(),event.getMainClassTop().getURL_site());
                if (!ClassDataBaseListMang.thereIsInTheDatabase(event.getMainClassTop().getName_characher())){
                    ClassDataBaseListMang.addBasaData(event.getMainClassTop(), -1);
                }
                processingOfDescriptionMang(event);
            }else {
                processingOfShowDownload(event);
            }
            myAdap.notifyDataSetChanged();
        }
    }


    private void processingOfDescriptionMang(ClassTransportForList event){
        ArrayList<ClassForList> arrayList = event.getClassForList();
        for (ClassForList b: arrayList){
            if (!b.getURL_chapter().contains("?mature=1")){
                b.setURL_chapter(b.getURL_chapter() + "?mature=1");
            }
            list.add(b);
        }
        classDataBaseViewedHead = new ClassDataBaseViewedHead(getActivity());
        if (classDataBaseViewedHead.addBasaData(event.getName())){
            String strings;
            strings = classDataBaseViewedHead.getDataFromDataBase(event.getName(),classDataBaseViewedHead.VIEWED_HEAD);
            if (!strings.contains("null")){
                String string[] = strings.split(",");
                for (String aString : string) {
                    ClassForList classForList1 = list.get(Integer.parseInt(aString));
                    classForList1.setCheck(true);
                    list.set(Integer.parseInt(aString), classForList1);
                }
            }
        }
    }

    private void processingOfShowDownload(ClassTransportForList event){
        for (ClassForList b: event.getClassForList())
            list.add(b);
        readDownloaded = true;
       /* classDataBaseViewedHead = new ClassDataBaseViewedHead(getActivity());
        if (classDataBaseViewedHead.addBasaData(event.getName())){
            String strings;
            strings = classDataBaseViewedHead.getDataFromDataBase(event.getName(),classDataBaseViewedHead.VIEWED_HEAD);
            if (!strings.contains("null")){
                String string[] = strings.split(",");
                for (String aString : string) {
                    ClassForList classForList1 = list.get(Integer.parseInt(aString));
                    classForList1.setCheck(true);
                    list.set(Integer.parseInt(aString), classForList1);
                }
            }
        }*/
    }

    public void onEvent(ClassDescriptionMang event) {
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
