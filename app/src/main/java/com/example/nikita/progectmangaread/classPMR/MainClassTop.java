package com.example.nikita.progectmangaread.classPMR;

import android.graphics.Bitmap;

/**
 * Created by Nikita on 01.01.2016.
 */
public class MainClassTop {
    String URL_characher;
    String name_characher;
    String URL_img;
    Bitmap img;

    public MainClassTop(){

    }

    public MainClassTop(String URL, String name,String img){
        URL_characher = URL;
        name_characher = name;
        URL_img = img;
    }


    public String getURL_characher(){ return URL_characher; }
    public String getName_characher() { return  name_characher; }
    public String getURL_img() {return URL_img; }
    public Bitmap getImg() { return img; }

    public void setImg(Bitmap img) { this.img = img; }
    public void setURL_characher(String url){ URL_characher = url; }
    public void setURL_img(String url) { URL_img = url; }
    public void setName_characher(String name) {name_characher = name; }

}
