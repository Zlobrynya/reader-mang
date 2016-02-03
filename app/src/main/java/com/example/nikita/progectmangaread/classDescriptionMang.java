package com.example.nikita.progectmangaread;

import android.graphics.Bitmap;

/**
 * Created by Nikita on 03.02.2016.
 */
public class classDescriptionMang {
    private String nameMang,nameAuthor,Rank,toms,genre,description;
    private Bitmap imgMang;

    classDescriptionMang() {
        nameAuthor = nameMang = nameMang = Rank = toms = genre = description = " ";
    }

    void setNameMang(String nameMang){
        this.nameMang = nameMang;
    }
    void setDescription(String des) {description = des;}
    void setNameAuthor(String author){ nameAuthor = author;}
    void setRank(String rank){ Rank = rank; }
    void addGenre(String genre){
        this.genre = genre;
    }
    void setImgMang(Bitmap imgMang){
        this.imgMang = imgMang;
    }
    void setToms(String toms){
        this.toms = toms;
    }

    public String getGenre(){return genre;}
    public String getNameMang() {return  nameMang;}
    public Bitmap getImgMang() {return imgMang;}
    public String getRank() {return Rank;}
    public String getNameAuthor() {return nameAuthor;}
    public String getToms() {return toms;}
    public String getDescription() {return description;}
}
