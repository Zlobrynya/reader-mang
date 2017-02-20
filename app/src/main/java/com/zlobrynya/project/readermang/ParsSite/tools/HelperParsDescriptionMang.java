package com.zlobrynya.project.readermang.ParsSite.tools;

import android.support.design.widget.FloatingActionButton;

import com.zlobrynya.project.readermang.Activity.BaseActivity;
import com.zlobrynya.project.readermang.ParsSite.Mangachan.ParsDescriptionMangMC;
import com.zlobrynya.project.readermang.ParsSite.ReadManga.ParsDescriptionMangRM;
import com.zlobrynya.project.readermang.classPMR.ClassDescriptionMang;
import com.zlobrynya.project.readermang.classPMR.ClassForList;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassOtherMang;
import com.zlobrynya.project.readermang.classPMR.ClassTransportForList;

import java.util.ArrayList;

/**
 * Created by Nikita on 20.02.2017.
 */

public class HelperParsDescriptionMang {
    private ParsDescriptionMangMC parsDescriptionMangMC;
    private ParsDescriptionMangRM parsDescriptionMangRM;
    private int numberSite = -1;

    public HelperParsDescriptionMang(BaseActivity baseActivity, ArrayList<ClassForList> arList, FloatingActionButton fab, boolean otherMang, ClassMainTop mang){
        if (mang.getUrlSite().contains("chan")){
            numberSite = 0;
            parsDescriptionMangMC = new ParsDescriptionMangMC(baseActivity,arList,fab,otherMang,mang);
        }else {
            numberSite = 1;
            parsDescriptionMangRM = new ParsDescriptionMangRM(baseActivity,arList,fab,otherMang,mang);
        }
    }

    public void setClass(ClassDescriptionMang classDescriptionMang, ClassTransportForList classTransportForList, ArrayList<ClassOtherMang> classOtherManglist){
        switch (numberSite){
            case 0:
                parsDescriptionMangMC.setClass(classDescriptionMang,classTransportForList,classOtherManglist);
                break;
            case 1:
                parsDescriptionMangRM.setClass(classDescriptionMang,classTransportForList,classOtherManglist);
                break;
            default:
                break;
        }
    }

    public void startPars(){
        switch (numberSite){
            case 0:
                parsDescriptionMangMC.startPars();
                break;
            case 1:
                parsDescriptionMangRM.startPars();
                break;
            default:
                break;
        }
    }

}
