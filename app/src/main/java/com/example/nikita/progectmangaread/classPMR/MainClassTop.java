package com.example.nikita.progectmangaread.classPMR;

import android.graphics.Bitmap;

/**
 * Created by Nikita on 01.01.2016.
 */
public class MainClassTop {
    Bitmap img_characher;
    String URL_characher;
    String name_characher;

    public MainClassTop(Bitmap img, String URL, String name){
        img_characher = img;
        URL_characher = URL;
        name_characher = name;
    }


    public void editClass(int wScr, int hScr){
        int w = wScr / 4;
        int h = hScr / 5;
        img_characher = Bitmap.createScaledBitmap(img_characher,w,h,true);
    }


    public Bitmap getImg_characher(){return  img_characher;}

    public String getURL_characher(){
        return URL_characher;
    }

    public String getName_characher() { return  name_characher; }

    void setImg_characher(Bitmap img){
        img_characher = img;
    }

    void setURL_characher(String url){
        URL_characher = url;
    }

}
