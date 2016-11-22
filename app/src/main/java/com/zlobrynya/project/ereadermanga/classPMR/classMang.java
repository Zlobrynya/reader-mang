package com.zlobrynya.project.ereadermanga.classPMR;

/**
 * Created by Nikita on 03.01.2016.
 */
public class ClassMang {
    private String URL,imgURL,nameURL, nameCell;
    private String whereAll,where,path,path2;
    private int maxInPage,numberPage;

    public ClassMang(){}

    public ClassMang(String URL, String imgURL, String nameURL, String nameCell, int maxInPage){
        this.URL = URL;
        this.imgURL = imgURL;
        this.nameCell = nameCell;
        this.nameURL = nameURL;
        this.maxInPage = maxInPage;
    }

    public String getURL(){ return URL; }
    public String getImgURL(){ return imgURL; }
    public String getNameURL(){ return nameURL; }
    public String getWhereAll(){ return whereAll; }
    public String getNameCell(){ return nameCell; }
    public String getWhere() {return where;}
    public String getPath() {return path;}
    public String getPath2() {return path2;}

    public int getMaxInPage() { return maxInPage; }


    //установка пути листа топа
    public void setWhereAll(String where,String path,int amt){
        this.whereAll = where + amt + path;
        this.where = where;
        this.path = path;
        numberPage = amt;
    }

    public void setWhereAll(String where,String path,String path2,int amt){
        this.whereAll = where + path + amt + path2;
        this.where = where;
        this.path = path;
        this.path2 = path2;
        numberPage = amt;
    }

    public void setWhereAll(String request) {whereAll = request;}
    public void setURL(String URL) { this.URL = URL; }
    public void setImgURL(String imgURL) { this.imgURL = imgURL; }
    public void setNameURL(String nameURL) { this.nameURL = nameURL; }
    public void setNameCell(String nameCell) { this.nameCell = nameCell; }
    public void setMaxInPage(int number) { maxInPage = number; }
    public void setWhere(String where) {this.where = where; }
    public void setPath(String path) {this.path = path; }
    public void setPath2(String path) {path2 = path;}

    public void editWhere(int amt){
        numberPage = amt;
        if (path2.isEmpty()) {
            this.whereAll = where + amt + path;
        }else {
            this.whereAll = where + path + (amt*maxInPage) + path2;
        }
    }
}
