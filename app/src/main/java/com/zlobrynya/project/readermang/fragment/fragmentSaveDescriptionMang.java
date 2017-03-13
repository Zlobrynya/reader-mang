package com.zlobrynya.project.readermang.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.zlobrynya.project.readermang.classPMR.ClassDescriptionMang;
import com.zlobrynya.project.readermang.classPMR.ClassOtherMang;
import com.zlobrynya.project.readermang.classPMR.ClassTransportForList;

import java.util.ArrayList;

/**
 * Created by Nikita on 13.07.2016.
 * Фрагмент для сохранения данных при повороте,активити DescriptionMang
 */
public class fragmentSaveDescriptionMang extends Fragment {

    private ClassTransportForList classTransportForList;
    private ClassDescriptionMang ClassDescriptionMang;
    private ClassMainTop mang;
    private ArrayList<ClassOtherMang> classOtherMang;

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

    public void setClassOtherMang(ArrayList<ClassOtherMang> classOtherMang){
        this.classOtherMang = classOtherMang;
    }

    public ArrayList<ClassOtherMang> getClassOtherMang() {
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