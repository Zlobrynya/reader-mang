package com.example.nikita.progectmangaread;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void Click(View view) {
        Intent newInten = new Intent(MainActivity.this,temple_pase.class);
        classMang readManga = new classMang("http://readmanga.me","[class=img] img[src]","[class=img] a","[class=tile col-sm-6]");
        readManga.setWhere("/list", "?type=&sortType=rate&offset=", "&max=70", 0);
        newInten.putExtra("URL", "http://readmanga.me");
        newInten.putExtra("Where","/list?type=&sortType=rate&offset=0&max=70");
        newInten.putExtra("Cell","[class=tile col-sm-6]");
        newInten.putExtra("nameURL", "[class=img] a");
        newInten.putExtra("nameIMG", "[class=img] img[src]");
        startActivity(newInten);
    }

    public void AdultManga(View view) {
        Intent newInten = new Intent(MainActivity.this,temple_pase.class);
        newInten.putExtra("URL","http://AdultManga.ru/");
        newInten.putExtra("Where","/list");
        newInten.putExtra("Cell","[class=tile col-sm-6]");
        newInten.putExtra("nameURL", "[class=img] a");
        newInten.putExtra("nameIMG", "[class=img] img[src]");
        startActivity(newInten);
    }

    public void mangafox(View view) {
        Intent newInten = new Intent(MainActivity.this,temple_pase.class);
        classMang mangafox = new classMang("http://mangafox.me","[class=manga_img] img[src]","[class=manga_img] ","[class=list] li");
        mangafox.setWhere("/directory/",".html",1);
        System.out.println("!!!!!!!!" + mangafox.getWhere());
        mangafox.editWhere(2);
        System.out.println("!!!!!!!!" + mangafox.getWhere());

        newInten.putExtra("URL","http://mangafox.me/");
        newInten.putExtra("Where","/directory");
        newInten.putExtra("Cell","[class=list] li");
        newInten.putExtra("nameURL", "[class=manga_img]");
        newInten.putExtra("nameIMG", "[class=manga_img] img[src]");
        startActivity(newInten);
    }
}
