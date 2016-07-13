package com.example.nikita.progectmangaread.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.example.nikita.progectmangaread.classPMR.MainClassTop;
import com.example.nikita.progectmangaread.classPMR.classDescriptionMang;
import com.example.nikita.progectmangaread.classPMR.classForList;
import com.example.nikita.progectmangaread.classPMR.classTransportForList;

import java.util.ArrayList;

/**
 * Created by Nikita on 13.07.2016.
 * Фрагмент для сохранения данных при повороте,активити DescriptionMang
 */
public class fragmentSaveDescriptionMang extends Fragment {

    private classTransportForList classTransportForList;
    private classDescriptionMang classDescriptionMang;
    private MainClassTop mang;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setRetainInstance(true);
    }

    public void setClassTransportForList(classTransportForList classTransportForList){
        this.classTransportForList = classTransportForList;
    }

    public void setClassDescriptionMang(classDescriptionMang classDescriptionMang){
        this.classDescriptionMang = classDescriptionMang;
    }

    public void setMang(MainClassTop mang){
        this.mang = mang;
    }

    public classTransportForList getClassTransportForList(){
        return classTransportForList;
    }

    public classDescriptionMang getClassDescriptionMang(){
        return classDescriptionMang;
    }

    public MainClassTop getMang(){
        return mang;
    }

}