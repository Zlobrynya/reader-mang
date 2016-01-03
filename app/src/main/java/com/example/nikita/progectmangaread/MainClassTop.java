package com.example.nikita.progectmangaread;

import android.graphics.Bitmap;

/**
 * Created by Nikita on 01.01.2016.
 */
public class MainClassTop {
    Bitmap img_characher;
    String URL_characher;

    MainClassTop(Bitmap img, String URL){
        img_characher = img;
        URL_characher = URL;
    }

    Bitmap getImg_characher(){
        return  img_characher;
    }

    String getURL_characher(){
        return URL_characher;
    }

    void setImg_characher(Bitmap img){
        img_characher = img;
    }

    void setURL_characher(String url){
        URL_characher = url;
    }

}
