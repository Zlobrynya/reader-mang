package com.example.nikita.progectmangaread.AdapterPMR;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.MainClassTop;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Nikita on 01.01.2016.
 * Class for Main screen top mang
 * класс для гравного экрана топа манг
 */
public class AdapterMainScreen extends ArrayAdapter<MainClassTop> {

    LinkedList<MainClassTop> item;

    public AdapterMainScreen(Context context, int resourse, LinkedList<MainClassTop> item) {
        super(context, resourse,item);
        this.item = item;
    }

    public void deleteFist(int kol){
        for (int i = 0; i < kol; i++){
            item.removeFirst();
        }
    }

    public void deleteLast(int kol){
        for (int i = 0; i < kol; i++){
            item.removeLast();
        }
    }


    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder=new Holder();
        if (v != null){
            holder = (Holder) v.getTag();
        }else {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_from_graund_view, null);
            holder.img = (ImageView)v.findViewById(R.id.imageView1);
            holder.tv = (TextView) v.findViewById(R.id.textMang);
            v.setTag(holder);
        }
        //получаем класс из позиции
        MainClassTop m1 = getItem(position);
        //если он есть то получаеи и устанавливаем изображение
        if (m1 != null){
            holder.img.setImageBitmap(m1.getImg_characher());
            holder.tv.setText(m1.getName_characher());
        }
        return v;
    }



}
