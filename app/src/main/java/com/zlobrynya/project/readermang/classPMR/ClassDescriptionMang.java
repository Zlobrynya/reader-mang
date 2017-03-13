package com.zlobrynya.project.readermang.classPMR;

/**
 * Created by Nikita on 03.02.2016.
 */

public class ClassDescriptionMang {
    private String nameMang,nameAuthor,Rank,toms,genre,description,translate,category,img_url;
    private boolean download = false;

    public ClassDescriptionMang() {
        nameAuthor = nameMang = Rank = toms = genre =  "";
        category = img_url = translate = description = "";

    }

    public void setNameMang(String nameMang){ this.nameMang = nameMang; }
    public void setDescription(String des) {description = des;}
    public void setNameAuthor(String author){ nameAuthor = author;}
    public void setRank(String rank){ Rank = rank; }
    public void setGenre(String genre){ this.genre = genre; }
    public void setToms(String toms){ this.toms = toms; }
    public void setTranslate(String translate) { this.translate = translate; }
    public void setCategory(String category) { this.category = category; }
    public void setImg_url(String url){ this.img_url = url;}
    public void setDownload(boolean download) { this.download = download; }

    public String getTranslate() {return translate;}
    public String getGenre(){return genre;}
    public String getNameMang() {return  nameMang;}
    public String getRank() {return Rank;}
    public String getNameAuthor() {return nameAuthor;}
    public String getToms() {return toms;}
    public String getDescription() {return description;}
    public String getCategory() { return category; }
    public String getImg_url(){ return this.img_url; }
    public boolean isDownload() { return download; }

}
