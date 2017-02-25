package com.zlobrynya.project.readermang.ParsSite.tools;

import android.content.Context;

import com.zlobrynya.project.readermang.AdapterPMR.AdapterMainScreen;
import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseListMang;
import com.zlobrynya.project.readermang.ParsSite.Mangachan.ParsTopListMC;
import com.zlobrynya.project.readermang.ParsSite.ReadManga.ParsTopListRM;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassMang;

import java.util.LinkedList;

/**
 * Created by Nikita on 17.02.2017.
 */

public class HelperParsTopList {
    private ParsTopListRM parsTopListRM;
    private ParsTopListMC parsTopListMC;
    private int numberSiteParse = -1;

    public HelperParsTopList(Context context, LinkedList<ClassMainTop> list){
        parsTopListMC = new ParsTopListMC(context,list);
        parsTopListRM = new ParsTopListRM(context,list);
    }

    public void setCallback(AsyncTaskLisen callback) {
        parsTopListMC.setCallback(callback);
        parsTopListRM.setCallback(callback);
    }

    public void setClassMang(ClassMang classMang){
        if (classMang.getURL().contains("chan")) {
            numberSiteParse = 0;
            parsTopListMC.setClassMang(classMang);
        }else{
            numberSiteParse = 1;
            parsTopListRM.setClassMang(classMang);
        }
    }

    public boolean isStopLoad(){
        switch (numberSiteParse) {
            case 0:
                return parsTopListMC.isStopLoad();
            case 1:
                return parsTopListRM.isStopLoad();
            default:
                break;
        }
        return false;
    }

    public void setResultPost(int resultPost){
        switch (numberSiteParse){
            case 0:
                parsTopListMC.setResultPost(resultPost);
                break;
            case 1:
                parsTopListRM.setResultPost(resultPost);
                break;
            default:
                break;
        }
    }


    public void startPars(int kol){
        switch (numberSiteParse){
            case 0:
                parsTopListMC.parsSite(kol);
                break;
            case 1:
                parsTopListRM.parssate(kol);
                break;
            default:
                break;
        }
    }

    public void setClassDataBaseListMang(ClassDataBaseListMang classDataBaseListMang){
        switch (numberSiteParse){
            case 0:
                parsTopListMC.setClassDataBaseListMang(classDataBaseListMang);
                break;
            case 1:
                parsTopListRM.setClassDataBaseListMang(classDataBaseListMang);
                break;
            default:
                break;
        }
    }

    public void setAdapter(AdapterMainScreen myAdap){
        parsTopListMC.setAdapter(myAdap);
        parsTopListRM.setAdapter(myAdap);
    }

    public void clearData(){
        switch (numberSiteParse){
            case 0:
                parsTopListMC.clearData();
                break;
            case 1:
                parsTopListRM.clearData();
                break;
            default:
                break;
        }
    }


}
