package com.zlobrynya.project.readermang.AdapterPMR;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zlobrynya.project.readermang.Activity.TopManga;
import com.zlobrynya.project.readermang.R;

import java.util.ArrayList;

/**
 * Created by Nikita on 26.03.2016.
 * Задел на будущее в закладках (группы
 */
public class AdapterSlidingMenu extends BaseAdapter {
    private ArrayList<String> str;
    private Context context;

    public AdapterSlidingMenu(Context context){
        this.context = context;

        str = new ArrayList<>();
        str.add("List Mang");
        str.add("Favorits");
        str.add("Dowland");
    }

    @Override
    public int getCount() {
        return str.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class Holder
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
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.item_sliding_menu, null);
            holder.img = (ImageView)v.findViewById(R.id.imageView2);
            holder.img.getLayoutParams().width = TopManga.WIDTH_WIND / 4;
            holder.img.getLayoutParams().height = TopManga.HEIGHT_WIND / 5;
            holder.tv = (TextView) v.findViewById(R.id.textView);
            v.setTag(holder);
        }

        holder.tv.setText(str.get(position));
        holder.img.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.launcher));
        return v;
    }
}
