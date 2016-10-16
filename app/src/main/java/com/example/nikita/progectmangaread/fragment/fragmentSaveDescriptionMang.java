package com.example.nikita.progectmangaread.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.example.nikita.progectmangaread.classPMR.ClassMainTop;
import com.example.nikita.progectmangaread.classPMR.ClassDescriptionMang;
import com.example.nikita.progectmangaread.classPMR.ClassOtherMang;
import com.example.nikita.progectmangaread.classPMR.ClassTransportForList;

/**
 * Created by Nikita on 13.07.2016.
 * Фрагмент для сохранения данных при повороте,активити DescriptionMang
 */
public class fragmentSaveDescriptionMang extends Fragment {

    private ClassTransportForList classTransportForList;
    private ClassDescriptionMang ClassDescriptionMang;
    private ClassMainTop mang;
    private ClassOtherMang classOtherMang;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setRetainInstance(true);
    }

    public void setClassTransportForList(ClassTransportForList classTransportForList){
        this.classTransportForList = classTransportForList;
    }

    public void setClassDescriptionMang(ClassDescriptionMang ClassDescriptionMang){
        this.ClassDescriptionMang = ClassDescriptionMang;
    }

    public void setClassOtherMang(ClassOtherMang classOtherMang){

    }

    public ClassOtherMang getClassOtherMang() {
        return classOtherMang;
    }

    public void setMang(ClassMainTop mang){
        this.mang = mang;
    }

    public ClassTransportForList getClassTransportForList(){
        return classTransportForList;
    }

    public ClassDescriptionMang getClassDescriptionMang(){
        return ClassDescriptionMang;
    }

    public ClassMainTop getMang(){
        return mang;
    }

}