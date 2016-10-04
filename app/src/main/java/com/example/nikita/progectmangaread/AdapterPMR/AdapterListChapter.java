package com.example.nikita.progectmangaread.AdapterPMR;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassForList;
import com.example.nikita.progectmangaread.classPMR.ClassRecentlyRead;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Nikita on 29.07.2016.
 */
public class AdapterListChapter extends ArrayAdapter<ClassForList> implements StickyListHeadersAdapter{

    private ArrayList<ClassForList> item;


    public AdapterListChapter(Context context, int resourse, ArrayList<ClassForList> item) {
        super(context, resourse,item);
        this.item = item;
    }

    private class Holder
    {
        TextView tv;
        CheckBox checkBox;
        ImageView imageView;
    }

    private class HeaderViewHolder {
        TextView text;
    }

    private String getNumberChapter(String nameChapter){
        String[] strings = nameChapter.split(" ");
        String numberChapter = "";
        for (int i = 1; i < strings.length; i++) {
            try {
                int num = Integer.parseInt(strings[i]);
                if (i+1<strings.length)
                    if(strings[i+1].contains("-") || strings[i+1].contains("Экстр")){
                        numberChapter += num;
                        break;
                    }
                //   Log.i(strLog, "number " + numberChapter);
            } catch (NumberFormatException e) {
                // Log.i(strLog, "Error number " + numberChapter);
            }
        }
        if (numberChapter.isEmpty())
            numberChapter = "0";
        return numberChapter;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.header_recently_read, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.head_recently_read);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        //set header text as first char in name
        String headerText = "Tom: " + getNumberChapter(item.get(position).getName_chapter());
        holder.text.setText(headerText);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return Long.parseLong(getNumberChapter(item.get(position).getName_chapter()));
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
                if (holder.imageView != null){
                    holder.imageView.setImageResource(R.drawable.ic_save_black_24dp);
                    ColorFilter filter = new LightingColorFilter( Color.GRAY, Color.GRAY );
                    holder.imageView.setColorFilter(filter);
                }
            }else {
                if (m1.getNewChapter()){
                    if (holder.imageView != null){
                        holder.imageView.setImageResource(R.drawable.ic_add_black_24dp);
                        ColorFilter filter = new LightingColorFilter( Color.GRAY, Color.GRAY );
                        holder.imageView.setColorFilter(filter);
                    }
                }
            }
        }
        return v;
    }
}
