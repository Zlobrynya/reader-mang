package com.example.nikita.progectmangaread;

import android.widget.ImageView;

/**
 * Created by Nikita on 03.01.2016.
 */
public class classMang {
    private String UML,imgUML,nameUML, nameCell;
    private String whereAll,where,putch,putch2;
    private int maxInPage,numberPage;
    classMang(){}

    classMang(String UML,String imgUML, String nameUML,String nameCell,int maxInPage){
        this.UML = UML;
        this.imgUML = imgUML;
        this.nameCell = nameCell;
        this.nameUML = nameUML;
        this.maxInPage = maxInPage;
    }

    String getUML(){ return UML; }

    String getImgUML(){ return imgUML; }

    String getNameUML(){ return nameUML; }

    String getWhere(){
        return whereAll;
    }

    String getNameCell(){
        return nameCell;
    }

    int getMaxInPage() {
        return maxInPage;
    }

    int getNumberPage() {return numberPage;}

    //установка пути листа топа
    void setWhere(String where,String putch,int amt){
        this.whereAll = where + amt + putch;
        this.where = where;
        this.putch = putch;
        numberPage = amt;
    }

    void setWhere(String where,String putch,String putch2,int amt){
        this.whereAll = where + putch + amt + putch2;
        this.where = where;
        this.putch = putch;
        this.putch2 = putch2;
        numberPage = amt;
    }


    void editWhere(int amt){
        numberPage = amt;
        if (putch2 == null) {
            this.whereAll = where + amt + putch;
        }else {
            this.whereAll = where + putch + (amt*maxInPage) + putch2;
        }
    }
}
