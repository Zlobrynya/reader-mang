package com.example.nikita.progectmangaread.AdapterPMR;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
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
        ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder=new Holder();
        if (v != null){
            holder = (Holder) v.getTag();
        }else {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_view_checkbox, null);
            holder.checkBox = (CheckBox)v.findViewById(R.id.check_box_list);
            holder.tv = (TextView) v.findViewById(R.id.list_name_chapter);
            holder.imageView = (ImageView) v.findViewById(R.id.ic_save_chapter);
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
            if (m1.getCheckDownload()){
                if (holder.imageView != null)
                    holder.imageView.setVisibility(View.VISIBLE);
            }else {
                holder.imageView.setVisibility(View.INVISIBLE);
            }
        }

        return v;
    }

}
