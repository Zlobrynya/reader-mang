package com.zlobrynya.project.ereadermanga.classPMR;

/**
 * Created by Nikita on 03.02.2016.
 */
public class ClassForList {
    private String URL_chapter;
    private String nameChapter;
    private boolean check,download,checkDownload,newChapter;
    private int numberChapter;

    public ClassForList() {
        check = download = checkDownload = newChapter = false;
    }

    public ClassForList(String URL_chapter, String name_chapter) {
        check = download = false;
        this.URL_chapter = URL_chapter;
        this.nameChapter = name_chapter;
    }

    public void setURL_chapter(String URL_chapter) { this.URL_chapter = URL_chapter; }
    public void setCheck(boolean check) { this.check = check; }
    public void setNameChapter(String name_chapter) { this.nameChapter = name_chapter; }
    public void setNumberChapter(int numberChapter) { this.numberChapter = numberChapter; }
    public void setDownload(boolean download){ this.download = download; }
    public void setCheckDownload(boolean check) { this.checkDownload = check; }
    public void setNewChapter(boolean newChapter) { this.newChapter = newChapter; }

    public String getURLChapter() { return URL_chapter; }
    public boolean isCheck() { return check;}
    public String getNameChapter() { return nameChapter; }
    public int getNumberChapter() { return numberChapter; }
    public boolean isDownload() { return download; }
    public boolean isCheckDownload() { return checkDownload; }
    public boolean isNewChapter() { return  newChapter; }
}
