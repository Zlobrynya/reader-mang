package com.zlobrynya.project.ereadermanga.classPMR;

/**
 * Created by Nikita on 01.01.2016.
 */
public class ClassMainTop {
    private String urlCharacher;
    private String nameCharacher;
    private String urlImg;
    private String urlSite;

    public ClassMainTop(){

    }

    public ClassMainTop(String URL, String name, String img, String URL_site){
        urlCharacher = URL;
        nameCharacher = name;
        urlImg = img;
        this.urlSite = URL_site;
    }


    public String getURLCharacher(){ return urlCharacher; }
    public String getNameCharacher() { return nameCharacher; }
    public String getUrlImg() {return urlImg; }
    public String getUrlSite() {return urlSite;}

    public void setUrlCharacher(String url){ urlCharacher = url; }
    public void setUrlImg(String url) { urlImg = url; }
    public void setNameCharacher(String name) {
        nameCharacher = name; }
    public void setUrlSite(String url_site) { urlSite = url_site; }
}
