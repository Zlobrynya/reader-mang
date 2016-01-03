package com.example.nikita.progectmangaread;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Nikita on 01.01.2016.
 * Class for Main screen top mang
 * класс для гравного экрана топа манг
 */
public class AdapterMainScreen extends ArrayAdapter<MainClassTop> {

    public AdapterMainScreen(temple_pase context, int resourse, ArrayList<MainClassTop> item) {
        super(context, resourse, item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ImageView img;
        if (v != null){
            img = (ImageView) v.getTag();
        }else {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.layout_from_graund_view, null);
            img = (ImageView)v.findViewById(R.id.imageView1);
            v.setTag(img);
        }
        //получаем класс из позиции
        MainClassTop m1 = getItem(position);
        //если он есть то получаеи и устанавливаем изображение
        if (m1 != null){
            img.setImageBitmap(m1.getImg_characher());
        }
        return v;
    }
}
