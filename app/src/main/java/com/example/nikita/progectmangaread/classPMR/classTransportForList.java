package com.example.nikita.progectmangaread.classPMR;

import java.util.ArrayList;

/**
 * Created by Nikita on 17.03.2016.
 */
public class classTransportForList {
    private ArrayList<classForList> cl;
    private String name;
    private MainClassTop mainClassTop;

    public classTransportForList(ArrayList<classForList> cl,String name,MainClassTop mainClassTop){
        this.cl = cl;
        this.name = name;
        this.mainClassTop = mainClassTop;
    }

    public String getName() { return name; }
    public ArrayList<classForList> getClassForList() {return cl;}
    public MainClassTop getMainClassTop() {return mainClassTop;}
}
