package com.example.nikita.progectmangaread.classPMR;

/**
 * Created by Nikita on 28.04.2016.
 */
public class classRecentlyRead {
    private String URL_img,nameChapter,nameMang,URLchapter,URLchapter_last;

    public classRecentlyRead(String urlImage,String nameMang,String nameChapter,String URLchapter,String URLchapter_last){
        this.URL_img = urlImage;
        this.nameChapter = nameChapter;
        this.nameMang = nameMang;
        this.URLchapter = URLchapter;
        this.URLchapter_last = URLchapter_last;
    }


    public String getURL_img(){
        return  URL_img;
    }

    public String getNameChapter(){
        return nameChapter;
    }

    public String getNameMang(){
        return nameMang;
    }

    public String getURLchapter() { return URLchapter; }

    public String getURLchapter_last() {return URLchapter_last;}

}
