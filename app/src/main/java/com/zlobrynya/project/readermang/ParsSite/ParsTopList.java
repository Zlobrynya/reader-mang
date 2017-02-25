package com.zlobrynya.project.readermang.ParsSite;

import android.content.Context;

import com.zlobrynya.project.readermang.AdapterPMR.AdapterMainScreen;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseListMang;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassMang;

import org.jsoup.nodes.Document;

import java.util.LinkedList;

/**
 * Created by Nikita on 30.01.2017.
 */

public class ParsTopList {
    protected Context context;
    protected Document doc;
    protected int resultPost;
    protected int page;
    protected boolean stopLoad;
    protected final boolean DEBUG = true;
    protected LinkedList<ClassMainTop> list;
    protected AdapterMainScreen myAdap;
    protected ClassDataBaseListMang classDataBaseListMang;
    protected ClassMang classMang;
    protected AsyncTaskLisen callback;

    public ParsTopList(Context context, LinkedList<ClassMainTop> list){
        this.context = context;
        this.list = list;
        resultPost = page = 0;
        stopLoad = false;
    }

    public void clearData(){
        doc = null;
        classMang = null;
        stopLoad = false;
        list.clear();
        myAdap.notifyDataSetChanged();
        page = 0;
    }

    public void setAdapter(AdapterMainScreen myAdap) {
        this.myAdap = myAdap;
    }

    public void setClassMang(ClassMang classMang) {
        this.classMang = classMang;
    }

    public void setResultPost(int resultPost) {
        this.resultPost = resultPost;
    }

    public void setClassDataBaseListMang(ClassDataBaseListMang classDataBaseListMang) {
        this.classDataBaseListMang = classDataBaseListMang;
    }

    public void setCallback(AsyncTaskLisen callback) {
        this.callback = callback;
    }

    public boolean isStopLoad() {
        return stopLoad;
    }
}
