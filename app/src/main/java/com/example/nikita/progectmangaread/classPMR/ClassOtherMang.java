package com.example.nikita.progectmangaread.classPMR;

/**
 * Created by Nikita on 08.10.2016.
 * Класс для передачи в fragmentOtherManga
 * Значений манг в один ArrayList и передача его
 *
 */

public class ClassOtherMang extends ClassRecentlyRead{
    private String nameCategory;
    private String urlSite;

    public ClassOtherMang() {
        super("", "", "", "", "");
        this.nameCategory = "";
    }

    public String getNameCategory(){
        return nameCategory;
    }
    public String getUrlSite() {
        return urlSite;
    }

    public void setNameCategory(String nameCategory){
        this.nameCategory = nameCategory;
    }
    public void setUrlSite(String urlSite) {
        this.urlSite = urlSite;
    }


}
