package com.example.nikita.progectmangaread.AdapterPMR;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikita.progectmangaread.Activity.TopManga;
import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseViewedHead;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.classPMR.ClassRecentlyRead;

import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.ArrayList;

/**
 * Created by Nikita on 03.05.2016.
 */
public class AdapterBookmark extends ArrayAdapter<ClassRecentlyRead> {
    private DisplayImageOptions options;
    private Context context;
    private  ArrayList<ClassRecentlyRead> item;

    public AdapterBookmark(Context context, int resource, ArrayList<ClassRecentlyRead> item) {
        super(context, resource, item);
        this.item = item;
        this.context = context;
        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                .threadPoolSize(3)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(8 * 1024 * 1024)) // 2 Mb
                .diskCache(new LimitedAgeDiskCache(context.getApplicationContext().getCacheDir(), null, new HashCodeFileNameGenerator(), 60 * 60 * 30))
                .imageDownloader(new BaseImageDownloader(context)) // connectTimeout (5 s), readTimeout (30 s)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();


        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.f) // resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    private class Holder {
        TextView nameMang;
        ImageView img;
        ImageButton buttonDelete;
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
            holder.img.setMinimumWidth(TopManga.WIDTH_WIND / 4);
            holder.img.setMinimumHeight(TopManga.HEIGHT_WIND / 5);
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
                ClassDataBaseViewedHead classDataBaseViewedHead = new ClassDataBaseViewedHead(context);
                classDataBaseViewedHead.setData(item.get(poss).getNameMang(), "0", ClassDataBaseViewedHead.NOTEBOOK);
                item.remove(poss);
                classDataBaseViewedHead.closeDataBase();
                notifyDataSetChanged();
                Toast.makeText(context, "Delete: " + item.get(poss).getNameMang(),
                        Toast.LENGTH_SHORT).show();
                //  Toast.makeText(TravelBite.this, "test", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }
}