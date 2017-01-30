package com.zlobrynya.project.readermang.ParsSite;

import android.support.design.widget.FloatingActionButton;

import com.zlobrynya.project.readermang.Activity.BaseActivity;
import com.zlobrynya.project.readermang.classPMR.ClassDescriptionMang;
import com.zlobrynya.project.readermang.classPMR.ClassForList;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassOtherMang;
import com.zlobrynya.project.readermang.classPMR.ClassTransportForList;

import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Nikita on 30.01.2017.
 */

public class ParsDescriptionMang {
    protected ClassDescriptionMang classDescriptionMang;
    protected ClassTransportForList classTransportForList;
    protected ArrayList<ClassOtherMang> classOtherManglist;
    protected ClassMainTop mang;
    protected ArrayList<ClassForList> arList;
    protected Document doc;
    protected BaseActivity baseActivity;
    protected FloatingActionButton fab;
    protected boolean otherMang = false;

    public ParsDescriptionMang(BaseActivity baseActivity, ArrayList<ClassForList> arList, FloatingActionButton fab, boolean otherMang){
        this.baseActivity = baseActivity;
        this.arList = arList;
        this.fab = fab;
        this.otherMang = otherMang;
    }

    public void setClass(ClassDescriptionMang classDescriptionMang,ClassTransportForList classTransportForList,ArrayList<ClassOtherMang> classOtherManglist,ClassMainTop mang){
        this.classDescriptionMang = classDescriptionMang;
        this.classTransportForList = classTransportForList;
        this.classOtherManglist = classOtherManglist;
        this.mang = mang;
    }
}
