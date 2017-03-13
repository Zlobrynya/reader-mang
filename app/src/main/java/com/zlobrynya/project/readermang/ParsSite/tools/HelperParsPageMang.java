package com.zlobrynya.project.readermang.ParsSite.tools;

import android.content.Context;

import com.zlobrynya.project.readermang.ParsSite.InterParsPageMang;
import com.zlobrynya.project.readermang.ParsSite.Mangachan.ParsPageMangMC;
import com.zlobrynya.project.readermang.ParsSite.ParsPageMang;
import com.zlobrynya.project.readermang.ParsSite.ReadManga.ParsPageMangRM;

import java.util.ArrayList;

/**
 * Created by Nikita on 21.02.2017.
 */

public class HelperParsPageMang {
    private ParsPageMangRM parsPageMangRM;
    private ParsPageMangMC parsPageMangMC;
    private int numberSite = -1;

    public HelperParsPageMang(String URL, ArrayList<String> urlPage, Context context){
        if (URL.contains("chan")){
            numberSite = 0;
            parsPageMangMC = new ParsPageMangMC(URL,urlPage,context);
        }else {
            numberSite = 1;
            parsPageMangRM = new ParsPageMangRM(URL,urlPage,context);
        }
    }

    public void addInterface(InterParsPageMang addImg){
        switch (numberSite){
            case 0:
                parsPageMangMC.addInterface(addImg);
                break;
            case 1:
                parsPageMangRM.addInterface(addImg);
                break;
            default:
                break;
        }
    }

    public void startPars(){
        switch (numberSite){
            case 0:
                parsPageMangMC.startPars();
                break;
            case 1:
                parsPageMangRM.startPars();
                break;
            default:
                break;
        }
    }

}
