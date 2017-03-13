package com.zlobrynya.project.readermang.ParsSite;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.zlobrynya.project.readermang.AsyncTaskLisen;
import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseViewedHead;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;

import java.util.ArrayList;

/**
 * Created by Nikita on 07.02.2017.
 */

public class ParsPageMang {
    protected String URL;
    protected ArrayList<String> urlPage;
    protected InterParsPageMang addImg;
    protected Context context;

    protected ParsPageMang(String URL, ArrayList<String> urlPage, Context context){
        this.URL = URL;
        this.urlPage = urlPage;
        this.context = context;
    }

    public void addInterface(InterParsPageMang addImg){
        this.addImg = addImg;
    }

}
