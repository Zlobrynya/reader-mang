package com.example.nikita.progectmangaread.classPMR;

import android.graphics.Bitmap;

/**
 * Created by Nikita on 01.01.2016.
 */
public class ClassMainTop {
    private String URL_characher;
    private String name_characher;
    private String URL_img;
    private String URL_site;

    public ClassMainTop(){

    }

    public ClassMainTop(String URL, String name, String img, String URL_site){
        URL_characher = URL;
        name_characher = name;
        URL_img = img;
        this.URL_site = URL_site;
    }


    public String getURL_characher(){ return URL_characher; }
    public String getName_characher() { return  name_characher; }
    public String getURL_img() {return URL_img; }
    public String getURL_site() {return  URL_site;}

    public void setURL_characher(String url){ URL_characher = url; }
    public void setURL_img(String url) { URL_img = url; }
    public void setName_characher(String name) {name_characher = name; }
    public void setURL_site(String url_site) { URL_site = url_site; }
}
