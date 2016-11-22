package com.zlobrynya.project.ereadermanga.classPMR;

/**
 * Created by Nikita on 23.02.2016.
 */
public class ClassTransport {
    private ClassMang classMang;
    private String URL_Search;

    public ClassTransport(){}

    public ClassTransport(ClassMang classMang, String url){
        this.classMang = classMang;
        this.URL_Search = url;
    }

    public ClassMang getClassMang(){ return classMang; }
    public String getURL_Search() { return URL_Search; }

    public void setClassMang(ClassMang classMang) { this.classMang = classMang; }
    public void setURL_Search(String url_search) { this.URL_Search = url_search; }
}
