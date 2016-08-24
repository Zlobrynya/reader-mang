package com.example.nikita.progectmangaread.classPMR;

/**
 * Created by Nikita on 03.02.2016.
 */
public class ClassForList {
    private String URL_chapter;
    private String Name_chapter;
    private boolean check,download,checkDownload,newChapter;
    private int numberChapter;

    public ClassForList() {
        check = false;
    }

    public ClassForList(String URL_chapter, String name_chapter) {
        check = download = false;
        this.URL_chapter = URL_chapter;
        this.Name_chapter = name_chapter;
    }
    public void setURL_chapter(String URL_chapter) { this.URL_chapter = URL_chapter; }
    public void setCheck(boolean check) { this.check = check; }
    public void setName_chapter(String name_chapter) { this.Name_chapter = name_chapter; }
    public void setNumberChapter(int numberChapter) { this.numberChapter = numberChapter; }
    public void setDownload(boolean download){ this.download = download; }
    public void setCheckDownload(boolean check) { this.checkDownload = check; }
    public void setNewChapter(boolean newChapter) { this.newChapter = newChapter; }


    public String getURL_chapter() { return URL_chapter; }
    public boolean getCheck() { return check;}
    public String getName_chapter() { return Name_chapter; }
    public int getNumberChapter() { return numberChapter; }
    public boolean getDownload() { return download; }
    public boolean getCheckDownload() { return checkDownload; }
    public boolean getNewChapter() { return  newChapter; }
}
