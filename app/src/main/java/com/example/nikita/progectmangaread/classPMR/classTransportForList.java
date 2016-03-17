package com.example.nikita.progectmangaread.classPMR;

import java.util.ArrayList;

/**
 * Created by Nikita on 17.03.2016.
 */
public class classTransportForList {
    ArrayList<classForList> cl;
    String name;
    public classTransportForList(ArrayList<classForList> cl,String name){
        this.cl = cl;
        this.name = name;
    }

    public String getName() { return name; }
    public ArrayList<classForList> getClassForList() {return cl;}
}
