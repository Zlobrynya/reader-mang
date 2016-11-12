package com.example.nikita.progectmangaread.AdapterPMR;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.Activity.TopManga;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseDownloadMang;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassRecentlyRead;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Nikita on 12.11.2016.
 */

public class AdapterDownload extends AdapterBookmark {
    public AdapterDownload(Context context, int resource, ArrayList<ClassRecentlyRead> item) {
        super(context, resource, item);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        Holder holder = new Holder();
        if (v != null) {
            holder = (Holder) v.getTag();
        } else {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.bookmark, null);
            holder.img = (ImageView) v.findViewById(R.id.imageView_notebook);
            holder.img.getLayoutParams().width = TopManga.WIDTH_WIND / 4;
            holder.img.getLayoutParams().height = TopManga.HEIGHT_WIND / 5;
            holder.buttonDelete = (ImageButton) v.findViewById(R.id.imageButton_delete);
            holder.nameMang = (TextView) v.findViewById(R.id.textView_name_notebook);
            v.setTag(holder);
        }
        holder.buttonDelete.setTag(position);
        //получаем класс из позиции
        ClassRecentlyRead m1 = getItem(position);
        //если он есть то получаеи и устанавливаем изображение
        if (m1 != null){
            ImageLoader.getInstance().displayImage(m1.getURL_img(), holder.img,options);
            holder.nameMang.setText(m1.getNameMang());
            // holder.nameChapter.setText(m1.getNameChapter());
        }
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int poss = (int) v.getTag();
                if (item.size() >= poss){
                    ClassDataBaseDownloadMang classData = new ClassDataBaseDownloadMang(context);
                    classData.deletRow(item.get(poss).getNameMang());
                    // classDataBaseViewedHead.closeDataBase();
                    Toast.makeText(context, "Delete: " + item.get(poss).getNameMang(),
                            Toast.LENGTH_SHORT).show();
                    item.remove(poss);
                    notifyDataSetChanged();
                }
                //  Toast.makeText(TravelBite.this, "test", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}
