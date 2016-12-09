package com.zlobrynya.project.readermang.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zlobrynya.project.readermang.Activity.DescriptionMang;
import com.zlobrynya.project.readermang.AdapterPMR.AdapterBookmark;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseListMang;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseViewedHead;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassRecentlyRead;

import java.util.ArrayList;

/**
 * Created by Nikita on 30.09.2016.
 */

public class fragmentBookmark extends Fragment {
    private ArrayList<ClassRecentlyRead> list;
    private ClassDataBaseViewedHead classDataBaseViewedHead;
    private ListView listView;
    private String nameTable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_view, null);
        list = new ArrayList<>();
        classDataBaseViewedHead = new ClassDataBaseViewedHead(getActivity());
        listView = (ListView) v.findViewById(R.id.listView);
        nameTable = getArguments().getString("nameTable");

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long id) {
                Intent intent = new Intent(getActivity(), DescriptionMang.class);
                ClassMainTop mainTop = getClassMainTop(position);
                if (mainTop != null) {
                    intent.putExtra("URL_ch", mainTop.getURLCharacher());
                    intent.putExtra("Url_img", mainTop.getUrlImg());
                    intent.putExtra("Name_ch", mainTop.getNameCharacher());
                    intent.putExtra("Url_site", mainTop.getUrlSite());
                    startActivity(intent);
                    Log.v("long clicked", "pos: " + position);
                }
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DescriptionMang.class);
                ClassMainTop mainTop = getClassMainTop(position);
                if (mainTop != null) {
                    intent.putExtra("URL_ch", mainTop.getURLCharacher());
                    intent.putExtra("Url_img", mainTop.getUrlImg());
                    intent.putExtra("Name_ch", mainTop.getNameCharacher());
                    intent.putExtra("Url_site", mainTop.getUrlSite());
                    intent.putExtra("read", true);
                    startActivity(intent);
                    Log.v("long clicked", "pos: " + position);
                }
            }
        });
        initializationNotebook();
        return v;
    }

    private ClassMainTop getClassMainTop(int pos) {
        if (pos > -1) {
            ClassMainTop classTop = new ClassMainTop();
            classTop.setNameCharacher(list.get(pos).getNameMang());
            //Костыли
            if (list.get(pos).getURLchapter().contains("readmang")) {
                classTop.setUrlSite("http://readmanga.me");
            }
            if (list.get(pos).getURLchapter().contains("mintmanga")) {
                classTop.setUrlSite("http://mintmanga.com");
            }
            if (list.get(pos).getURLchapter().contains("selfmanga")) {
                classTop.setUrlSite("http://selfmanga.ru");
            }
            //
            classTop.setUrlImg(list.get(pos).getURL_img());
            classTop.setUrlCharacher(list.get(pos).getURLchapter());
            return classTop;
        }
        return null;
    }

    private void initializationNotebook() {
        Cursor cursor = classDataBaseViewedHead.getNotebook(nameTable);
        Log.i("Cursor: ", String.valueOf(cursor.getCount()));
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            String nameMang, nameChapter, URLchapter, URLimg, URLlastChapter;
            nameMang = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.NAME_MANG));
            URLchapter = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.URL_CHAPTER));
            URLlastChapter = cursor.getString(cursor.getColumnIndex(ClassDataBaseViewedHead.LAST_CHAPTER));
            URLimg = cursor.getString(cursor.getColumnIndex(ClassDataBaseListMang.URL_IMG));
            nameChapter = cursor.getString(cursor.getColumnIndex(ClassDataBaseViewedHead.NAME_LAST_CHAPTER));
            list.add(new ClassRecentlyRead(URLimg, nameMang, nameChapter, URLchapter, URLlastChapter));
            cursor.moveToNext();
        }
        cursor.close();
        AdapterBookmark adapter = new AdapterBookmark(getActivity(), R.layout.list_view, list);
        listView.setAdapter(adapter);
    }

    //фабричный метод для ViewPage
    public static fragmentBookmark newInstance(String nameTable) {
        fragmentBookmark fragment = new fragmentBookmark();
        Bundle args = new Bundle();
        args.putString("nameTable", nameTable);
        fragment.setArguments(args);
        return fragment;
    }
}