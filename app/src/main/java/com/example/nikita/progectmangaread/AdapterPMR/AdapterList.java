package com.example.nikita.progectmangaread.AdapterPMR;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassForList;

import java.util.ArrayList;

/**
 * Created by Nikita on 03.02.2016.
 */
public class AdapterList extends ArrayAdapter<ClassForList> {

    public AdapterList(Context context, int resourse, ArrayList<ClassForList> item) {
        super(context, resourse,item);
    }

    public class Holder
    {
        TextView tv;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder=new Holder();

        if (v != null){
            holder = (Holder) v.getTag();
        }else {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_for_list_view, null);
            holder.checkBox = (CheckBox)v.findViewById(R.id.checkBoxList);
            holder.tv = (TextView) v.findViewById(R.id.listNameChapter);
            v.setTag(holder);
        }
        //получаем класс из позиции
        ClassForList m1 = this.getItem(position);
        //если он есть то получаеи и устанавливаем
        if (m1 != null){
            if (m1.getCheck())
                holder.checkBox.setChecked(true);
            else holder.checkBox.setChecked(false);
            holder.tv.setText(m1.getName_chapter());
        }

        return v;
    }

}
