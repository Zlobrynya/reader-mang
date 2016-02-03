package com.example.nikita.progectmangaread.classPMR;

/**
 * Created by Nikita on 03.02.2016.
 */
public class classForList {
    String URL_chapter;
    String Name_chapter;
    boolean check;

    public classForList() {
        check = false;
    }

    public classForList(String URL_chapter,String name_chapter) {
        check = false;
        this.URL_chapter = URL_chapter;
        this.Name_chapter = name_chapter;
    }
    public void setURL_chapter(String URL_chapter) { this.URL_chapter = URL_chapter; }
    public void setCheck(boolean check) { this.check = check; }
    public void setName_chapter(String name_chapter) { this.Name_chapter = name_chapter; }

    public String getURL_chapter() { return URL_chapter; }
    public boolean getCheck() { return check;}
    public String getName_chapter() { return Name_chapter; }

}
