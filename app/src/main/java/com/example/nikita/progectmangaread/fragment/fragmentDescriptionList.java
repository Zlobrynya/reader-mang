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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Nikita on 03.02.2016.
 *
 */
public class fragmentDescriptionList extends Fragment {
    private final static String strLog = "Fragment Description List";
    private View v;
    private ArrayList<ClassForList> list;
    private AdapterList myAdap;
    private ListView listView;
    private String nameMang,imgURL;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private ClassDataBaseListMang ClassDataBaseListMang;
    private boolean readDownloaded;
    private final String PROBLEM = "ProblemTime";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        readDownloaded = false;
        myAdap = new AdapterList(getActivity(), R.layout.list_view_checkbox, list);
        EventBus.getDefault().register(this);
        Log.i(PROBLEM, "Create fragmentDescriptionList");

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_heads, null);
        listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(myAdap);
        final ClassForList classForList = new ClassForList();
        classForList.setName_chapter("GG");
       // Log.i(PROBLEM, "Start fragmentDescriptionList");


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ClassForList classForList1 = list.get(position);
                classForList1.setCheck(true);
                classForList1.setNumberChapter(position);
                list.set(position, classForList1);
                myAdap.notifyDataSetChanged();
                String[] strings = classForList1.getName_chapter().split(" ");
                String numberChapter = " ";
                int kolInt = 0;
                for (int i = 0; i < strings.length; i++) {
                    try {
                        int num = Integer.parseInt(strings[i]);
                        if (kolInt < 2) {
                            numberChapter += num;
                            kolInt++;
                        }
                     //   Log.i(strLog, "number " + numberChapter);
                    } catch (NumberFormatException e) {
                        if (strings[i].contains("-") && !numberChapter.contains("-"))
                            numberChapter += " - ";
                        if (strings[i].contains("Экстр")) {
                            if (i + 1 < strings.length)
                                numberChapter += " " + strings[i] + " " + strings[i + 1];
                            else
                                numberChapter += " " + strings[i];
                        }
                       // Log.i(strLog, "Error number " + numberChapter);
                    }
                }


                classDataBaseViewedHead.addViewedChapter(nameMang, numberChapter + " ");

                //Получаем дату когда тыкнули главу и загрузили в бд
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c.getTime());
                classDataBaseViewedHead.setData(nameMang, formattedDate, ClassDataBaseViewedHead.DATA);

                Log.i("Data", formattedDate);
                if (readDownloaded)
                    classForList1.setDownload(true);

                EventBus.getDefault().post(classForList1);
            }
        });
     //   Log.i(PROBLEM, "End Start fragmentDescriptionList");
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
        if (!readDownloaded)
            classDataBaseViewedHead.addViewedChapter(nameMang, String.valueOf(event));
        else
            classForList1.setDownload(true);
        EventBus.getDefault().post(classForList1);
    }

    public void onEvent(java.lang.String event){
        if (event.contains("notebook")){
            int notebook = Integer.parseInt(classDataBaseViewedHead.getDataFromDataBase(nameMang, classDataBaseViewedHead.NOTEBOOK));
            if (notebook == 0){
                classDataBaseViewedHead.setData(nameMang, String.valueOf(1), classDataBaseViewedHead.NOTEBOOK);
                String lastChapter = classDataBaseViewedHead.getDataFromDataBase(nameMang, classDataBaseViewedHead.LAST_CHAPTER);
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
        Log.i(strLog,"event ClassTransportForList");
        if (!event.getName().isEmpty()){
            list.clear();
            nameMang = event.getName();
            //Проверка откуда пришел евент
            if (event.getMainClassTop() != null){
                ClassDataBaseListMang = new ClassDataBaseListMang(getActivity(),event.getMainClassTop().getURL_site());
                if (!ClassDataBaseListMang.thereIsInTheDatabase(event.getMainClassTop().getName_characher())){
                    ClassDataBaseListMang.addBasaData(event.getMainClassTop(), -1);
                }
                ArrayList<ClassForList> arrayList = event.getClassForList();
                for (ClassForList b: arrayList){
                    if (!b.getURL_chapter().contains("?mature=1")){
                        b.setURL_chapter(b.getURL_chapter() + "?mature=1");
                    }
                    list.add(b);
                }
            }else {
                for (ClassForList b: event.getClassForList())
                    list.add(b);
                readDownloaded = true;
            }
            checkChapter(event);
            myAdap.notifyDataSetChanged();
        }
    }

    private void checkChapter(ClassTransportForList event){
        classDataBaseViewedHead = new ClassDataBaseViewedHead(getActivity());
        List<String> arrayListString = new ArrayList<>();
        if (classDataBaseViewedHead.addBasaData(event.getName())){
            String strings = classDataBaseViewedHead.getDataFromDataBase(event.getName(),classDataBaseViewedHead.VIEWED_HEAD);
            if (!strings.contains("null")){
                arrayListString = Arrays.asList(strings.split(","));
                //проходимся по списку глав
                for (int numbr = 0; numbr < list.size();numbr++){
                    ClassForList classForList = list.get(numbr);
                    //проходися по списку строк где указаны просмотреенные главы
                    for (String aString: arrayListString){
                        //если есть совпадение то ставим галочку и удалем этот элемент из массива строк
                        if (classForList.getName_chapter().contains(aString)){
                            classForList.setCheck(true);
                            list.set(numbr, classForList);
                            break;
                        }
                    }
                }

            }
        }
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
