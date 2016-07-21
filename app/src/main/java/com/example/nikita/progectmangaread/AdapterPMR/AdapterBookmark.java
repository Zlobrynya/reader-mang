package com.example.nikita.progectmangaread.AdapterPMR;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
    protected ImageLoader imageLoader;
    int w, h;
    private DisplayImageOptions options;


    public AdapterBookmark(Context context, int resource, ArrayList<ClassRecentlyRead> item, int w, int h) {
        super(context, resource, item);
        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                .threadPoolSize(3)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(8 * 1024 * 1024)) // 2 Mb
                .diskCache(new LimitedAgeDiskCache(context.getApplicationContext().getCacheDir(), null, new HashCodeFileNameGenerator(), 60 * 60 * 30))
                .imageDownloader(new BaseImageDownloader(context)) // connectTimeout (5 s), readTimeout (30 s)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();

        this.w = w;
        this.h = h;

        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.launcher) // resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public class Holder {
        TextView nameMang;
        ImageView img;
        ImageButton buttonDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder = new Holder();
        if (v != null) {
            holder = (Holder) v.getTag();
        } else {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.bookmark, null);
            holder.img = (ImageView) v.findViewById(R.id.imageView_notebook);
            holder.img.setMinimumWidth(w / 4);
            holder.img.setMinimumHeight(h / 5);
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
        return v;
    }
}