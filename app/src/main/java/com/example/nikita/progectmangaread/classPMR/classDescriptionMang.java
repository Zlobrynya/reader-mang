package com.example.nikita.progectmangaread.classPMR;

import android.graphics.Bitmap;

/**
 * Created by Nikita on 03.02.2016.
 */
public class classDescriptionMang {
    private String nameMang,nameAuthor,Rank,toms,genre,description,translate,category;
    private Bitmap imgMang;

    public classDescriptionMang() {
        nameAuthor = nameMang = Rank = toms = genre = description = translate = " ";
    }

    public void setNameMang(String nameMang){ this.nameMang = nameMang; }
    public void setDescription(String des) {description = des;}
    public void setNameAuthor(String author){ nameAuthor = author;}
    public void setRank(String rank){ Rank = rank; }
    public void setGenre(String genre){ this.genre = genre; }
    public void setImgMang(Bitmap imgMang){ this.imgMang = imgMang; }
    public void setToms(String toms){ this.toms = toms; }
    public void setTranslate(String translate) { this.translate = translate; }
    public void setCategory(String category) { this.category = category; }

    public String getTranslate() {return translate;}
    public String getGenre(){return genre;}
    public String getNameMang() {return  nameMang;}
    public Bitmap getImgMang() {return imgMang;}
    public String getRank() {return Rank;}
    public String getNameAuthor() {return nameAuthor;}
    public String getToms() {return toms;}
    public String getDescription() {return description;}
    public String getCategory() { return category; }
}
