package com.example.nikita.progectmangaread.fragment;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.nikita.progectmangaread.AdapterPMR.AdapterListChapter;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseDownloadMang;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseListMang;
import com.example.nikita.progectmangaread.classPMR.ClassForList;
import com.example.nikita.progectmangaread.classPMR.ClassMainTop;
import com.example.nikita.progectmangaread.classPMR.ClassTransportForList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Nikita on 03.02.2016.
 *
 */
public class fragmentDescriptionList extends Fragment {
    private final String strLog = "Fragment Description List";
    private ArrayList<ClassForList> list;
    private AdapterListChapter myAdap;
    private String nameMang;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private boolean readDownloaded;
    private ClassMainTop mainTop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        readDownloaded = false;
        myAdap = new AdapterListChapter(getActivity(), R.layout.list_view_checkbox, list);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sticky_list, null);
        StickyListHeadersListView listView = (StickyListHeadersListView) v.findViewById(R.id.listRecentlyRead);
        listView.setAdapter(myAdap);
        final ClassForList classForList = new ClassForList();
        classForList.setNameChapter("GG");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                ClassForList classForList1 = list.get(position);
                classForList1.setCheck(true);
                classForList1.setNumberChapter(position);
                list.set(position, classForList1);
                myAdap.notifyDataSetChanged();

                //высчитываем HASH_CODE строки
                String numberChapter = String.valueOf(classForList1.getNameChapter().hashCode());
                classDataBaseViewedHead.addViewedChapter(nameMang, numberChapter);

             //   String lastChapter = classDataBaseViewedHead.getDataFromDataBase(mainTop.getNameCharacher(),ClassDataBaseViewedHead.LAST_CHAPTER);
                if (mainTop != null)
                    classDataBaseViewedHead.editLastChapter(nameMang, mainTop.getUrlSite() + classForList1.getURLChapter());
                //Получаем дату когда тыкнули главу и загрузили в бд
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c.getTime());
                classDataBaseViewedHead.setData(nameMang, formattedDate, ClassDataBaseViewedHead.DATA);

                Log.i("Data", formattedDate);
                if (readDownloaded)
                    classForList1.setDownload(true);

                EventBus.getDefault().postSticky(classForList1);
            }
        });
     //   Log.i(PROBLEM, "End Start fragmentDescriptionList");
        return v;
    }

    //"Посылка" с fragmentPageDowland, что надо переключить главу
    @Subscribe(sticky = true,threadMode = ThreadMode.BACKGROUND)
    public void onEvent(java.lang.Integer event){
        ClassForList classForList1 = list.get(event);
        classForList1.setNumberChapter(event);
        if (!classForList1.isCheck())
            classForList1.setCheck(true);
        list.set(event, classForList1);
        classDataBaseViewedHead.addViewedChapter(nameMang, String.valueOf(classForList1.getNameChapter().hashCode()));
        if (!readDownloaded){
            classForList1.setDownload(false);
        }
        else
            classForList1.setDownload(true);
        if (mainTop != null)
            classDataBaseViewedHead.editLastChapter(nameMang, mainTop.getUrlSite() + classForList1.getURLChapter());
        //удалем sticky евент, что бы заново сюда не попадать
        EventBus.getDefault().removeStickyEvent(event);
        EventBus.getDefault().postSticky(classForList1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(java.lang.String event){
        if (event.contains("notebook")){
         /*   String locNameChar = nameMang;
            locNameChar = locNameChar.replace(")","").replace(" ","").replace("(",",").split(",")[1];
            locNameChar = "%(" + locNameChar + ")%";*/

            int notebook = Integer.parseInt(classDataBaseViewedHead.getDataFromDataBase(nameMang, ClassDataBaseViewedHead.NOTEBOOK));
            if (notebook == 0){
                classDataBaseViewedHead.setData(nameMang, String.valueOf(1), ClassDataBaseViewedHead.NOTEBOOK);
                String lastChapter = classDataBaseViewedHead.getDataFromDataBase(nameMang, ClassDataBaseViewedHead.LAST_CHAPTER);
                if (lastChapter.contains("null")){
                    classDataBaseViewedHead.editLastChapter(nameMang, list.get(list.size() - 1).getURLChapter());
                    classDataBaseViewedHead.setData(nameMang, list.get(list.size() - 1).getNameChapter(), ClassDataBaseViewedHead.NAME_LAST_CHAPTER);
                }
            }else {
                classDataBaseViewedHead.setData(nameMang, String.valueOf(0), ClassDataBaseViewedHead.NOTEBOOK);
            }
        }
    }

    //тут посылка с DescriptionMang, что надо бы добавить в list и обновить адаптер
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClassTransportForList event) {
       // Log.i(strLog, "event ClassTransportForList");
        if (!event.getName().isEmpty() && list.isEmpty()){
            //list.clear();
            nameMang = event.getName();
            mainTop = event.getMainClassTop();
            //Проверка откуда пришел евент
            if (mainTop != null){
                ClassDataBaseListMang classDataBaseListMang = new ClassDataBaseListMang(getActivity(), event.getMainClassTop().getUrlSite());
                if (!classDataBaseListMang.thereIsInTheDatabase(event.getMainClassTop().getNameCharacher())){
                    classDataBaseListMang.addBasaData(event.getMainClassTop(), -1);
                }
                for (ClassForList b: event.getClassForList()){
                    if (!b.getURLChapter().contains("?mature=1")){
                        b.setURL_chapter(b.getURLChapter() + "?mature=1");
                    }
                    list.add(b);
                }
               // classDataBaseListMang.closeDataBase();
            }else {
                //тут для чтения из сохраненых
                for (ClassForList b: event.getClassForList())
                    list.add(b);
                readDownloaded = true;
            }
            myAdap.notifyDataSetChanged();
            TheardCheckHeads theardCheckHeads = new TheardCheckHeads();
            theardCheckHeads.execute();
        }
    }

    @Override
    public void onStart() {
       // Log.i(strLog,"Start");
        EventBus.getDefault().register(this);
        myAdap.notifyDataSetChanged();
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy(){
       /* if (classDataBaseViewedHead != null)
            classDataBaseViewedHead.closeDataBase();*/
       // EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        if (!list.isEmpty()){
            TheardCheckHeads theardCheckHeads = new TheardCheckHeads();
            theardCheckHeads.execute();
        }
        super.onResume();
    }

    //фабричный метод для ViewPage
    public static fragmentDescriptionList newInstance(int page) {
        fragmentDescriptionList fragment = new fragmentDescriptionList();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    private class TheardCheckHeads extends AsyncTask<Void,Void,Void>{
        private boolean err = false;
        @Override
        protected Void doInBackground(Void... params) {
            checkChapter();
            return null;
        }

        private void checkChapter() {
            try {
                classDataBaseViewedHead = new ClassDataBaseViewedHead(getActivity());
            }catch (NullPointerException e){
                err = true;
                return;
            }
            ArrayList<Integer> hashCodeViewedHead = new ArrayList<>();
            ArrayList<Integer> hashCodeDownloadHead = new ArrayList<>();

            @SuppressLint("UseSparseArrays") HashMap<Integer,Integer> hashCodeHead = new HashMap<>();
            if (classDataBaseViewedHead.addBasaData(nameMang)) {
                initArray(hashCodeViewedHead,hashCodeDownloadHead);
                //Список глав переводим в ХэшКод
                for (int i = 0; i < list.size(); i++) {
                    ClassForList classForList = list.get(i);
                    hashCodeHead.put(i,classForList.getNameChapter().hashCode());
                }
                //Сортировка HashMap
                ValueComparatorMap valueComparatorMap = new ValueComparatorMap(hashCodeHead);
                TreeMap<Integer, Integer> integerTreeMap = new TreeMap<Integer, Integer>(valueComparatorMap);
                integerTreeMap.putAll(hashCodeHead);
                //Начинаем проходится по элементам
                for (Map.Entry<Integer,Integer> pair: integerTreeMap.entrySet()) {
                    // for (int i = 0; i < integerTreeMap.size();i++) {
                    // Log.i("integerTreeMap: ",pair.getKey()+":"+pair.getValue());
                    //сравниваем по ХэшеКоду c прочитынами
                    int key = pair.getKey();
                    ClassForList classForList = list.get(key);
                    classForList.setCheck(false);

                    if (hashCodeViewedHead.size() > 0){
                        if (pair.getValue().equals(hashCodeViewedHead.get(0))){
                            // Log.i("hashCodeViewedHead: ", String.valueOf(hashCodeViewedHead.get(0)));
                            classForList.setCheck(true);
                            hashCodeViewedHead.remove(0);
                        }
                    }
                    //сравниваем по ХэшеКоду c скачаными
                    if (hashCodeDownloadHead.size() > 0){
                        if (pair.getValue().equals(hashCodeDownloadHead.get(0))){;
                            classForList.setCheckDownload(true);
                            hashCodeDownloadHead.remove(0);
                        }
                    }
                    list.set(key,classForList);
                }

                if (hashCodeViewedHead.size() > 0){
                    additionalSearch(integerTreeMap,hashCodeViewedHead);
                }

                int quantity = Integer.parseInt(classDataBaseViewedHead.getDataFromDataBase(nameMang, ClassDataBaseViewedHead.NUMBER_OF_HEADS));
                if (list.size() - quantity > 0) {
                    for (int i = 0; i < list.size() - quantity; i++) {
                        ClassForList classForList = list.get(i);
                        classForList.setNewChapter(true);
                        list.set(i,classForList);
                    }
                }
            }
        }

        private void additionalSearch(TreeMap<Integer, Integer> integerTreeMap,ArrayList<Integer> hashCode){
            for (Map.Entry<Integer,Integer> pair: integerTreeMap.entrySet()) {
                int key = pair.getKey();
                ClassForList classForList = list.get(key);
                if (!classForList.isCheck()){
                    for (Integer hashCodeItem: hashCode){
                        if (pair.getValue().equals(hashCodeItem)){
                            classForList.setCheck(true);
                            hashCode.remove(hashCode.indexOf(hashCodeItem));
                            break;
                        }
                    }
                }
            }
        }


        //Инициализация ArryList для просмотренных и скачиных глав
        private void initArray(ArrayList<Integer> hashCodeViewedHead,ArrayList<Integer> hashCodeDownloadHead){
            //Достаем список глав манги которые были прочитаны и сортируем
            String stringHead = classDataBaseViewedHead.getDataFromDataBase(nameMang, ClassDataBaseViewedHead.VIEWED_HEAD);
            if (!stringHead.contains("null")) {
                for (String s : stringHead.split(",")) {
                    hashCodeViewedHead.add(Integer.parseInt(s));
                }
                // Sorting по возрастанию
                Collections.sort(hashCodeViewedHead, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer lhs, Integer rhs)
                    {
                        return  rhs.compareTo(lhs);
                    }
                });
            }
            //Достаем список глав манги которые были скачаны и сортируем
            ClassDataBaseDownloadMang downloadMang = new ClassDataBaseDownloadMang(getContext());
            stringHead =  downloadMang.getDataFromDataBase(nameMang,ClassDataBaseDownloadMang.NAME_CHAPTER);
          //  downloadMang.closeDataBase();
            if ((stringHead != null))
                if (!stringHead.contains("null")) {
                    for (String s : stringHead.split(",")) {
                        hashCodeDownloadHead.add(s.hashCode());
                    }
                    // Sorting по возрастанию
                    Collections.sort(hashCodeDownloadHead, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer lhs, Integer rhs) {
                            return rhs.compareTo(lhs);
                        }
                    });
                }
        }

        class ValueComparatorMap implements Comparator<Integer> {
            Map<Integer, Integer> base;

            ValueComparatorMap(Map<Integer, Integer> base) {
                this.base = base;
            }
            public int compare(Integer a, Integer b) {
                if (base.get(a) >= base.get(b)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }

        @Override
        protected void onPostExecute(Void result){
            if (!err){
                if (nameMang != null && classDataBaseViewedHead != null)
                    classDataBaseViewedHead.setData(nameMang, String.valueOf(list.size()), ClassDataBaseViewedHead.NUMBER_OF_HEADS);
                myAdap.notifyDataSetChanged();
            }
        }
    }
}
