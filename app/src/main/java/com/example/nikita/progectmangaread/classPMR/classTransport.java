package com.example.nikita.progectmangaread.classPMR;

/**
 * Created by Nikita on 23.02.2016.
 */
public class classTransport {
    classMang classMang;
    String URL_Search;

    public classTransport(){}

    public classTransport(classMang classMang,String url){
        this.classMang = classMang;
        this.URL_Search = url;
    }

    public classMang getClassMang(){ return classMang; }
    public String getURL_Search() { return URL_Search; }

    public void setClassMang(classMang classMang) { this.classMang = classMang; }
    public void setURL_Search(String url_search) { this.URL_Search = url_search; }
}
