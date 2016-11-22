package com.zlobrynya.project.ereadermanga.classPMR;

import java.util.ArrayList;

/**
 * Created by Nikita on 17.03.2016.
 */
public class ClassTransportForList {
    private ArrayList<ClassForList> cl;
    private String name;
    private ClassMainTop mainClassTop;

    public ClassTransportForList(ArrayList<ClassForList> cl, String name, ClassMainTop mainClassTop){
        this.cl = cl;
        this.name = name;
        this.mainClassTop = mainClassTop;
    }

    public String getName() { return name; }
    public ArrayList<ClassForList> getClassForList() {return cl;}
    public ClassMainTop getMainClassTop() {return mainClassTop;}
}
