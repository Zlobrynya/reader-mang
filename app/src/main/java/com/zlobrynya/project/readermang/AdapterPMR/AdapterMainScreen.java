package com.zlobrynya.project.readermang.AdapterPMR;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.classPMR.ClassMainTop;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.util.LinkedList;

/**
 * Created by Nikita on 01.01.2016.
 * Class for Main screen top mang
 * класс для гравного экрана топа манг
 */
public class AdapterMainScreen extends ArrayAdapter<ClassMainTop> {

    private int w,h;
    private DisplayImageOptions options;

    public AdapterMainScreen(Context context, int resourse, LinkedList<ClassMainTop> item,int w, int h) {
        super(context, resourse,item);
        this.w = w;
        this.h = h;
        File cacheDir = new File(context.getApplicationContext().getCacheDir(),"cacheGlavTop");

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                .threadPoolSize(3)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(8 * 1024 * 1024)) // 2 Mb
                .diskCache(new LimitedAgeDiskCache(cacheDir, null, new HashCodeFileNameGenerator(), 60 * 60 * 30))
                .imageDownloader(new BaseImageDownloader(context)) // connectTimeout (5 s), readTimeout (30 s)
                .build();

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.f) // resource or drawable
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }


    private class Holder
    {
        TextView tv;
        ImageView img;
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
            v = vi.inflate(R.layout.layout_from_graund_view, null);
            holder.img = (ImageView)v.findViewById(R.id.imageView1);
            holder.img.getLayoutParams().width = w;
            holder.img.getLayoutParams().height = h;
            holder.tv = (TextView) v.findViewById(R.id.textMang);
            v.setTag(holder);
        }
        //получаем класс из позиции
        try{
            ClassMainTop m1 = getItem(position);
            //если он есть то получаеи и устанавливаем изображение
            if (m1 != null){
                //проверка есть ли это изображение и изменилось ли оно
                //если нет то ничего не трогаем если да то грузим изображение
                //исправляет "баг" мерцание
                if (holder.img.getTag() == null ||
                        !holder.img.getTag().equals(m1.getUrlImg())) {
                    ImageLoader.getInstance().displayImage(m1.getUrlImg(), holder.img, options);
                    holder.img.setTag(m1.getUrlImg());
                    holder.tv.setText(m1.getNameCharacher());
                }
            }
        }catch (IndexOutOfBoundsException e){
            Crashlytics.logException(e);
            if (getCount() > 0){
                Crashlytics.setString("site",getItem(0).getUrlSite());
                Crashlytics.setString("characher",getItem(0).getURLCharacher());
                Crashlytics.setString("name",getItem(0).getNameCharacher());
                Crashlytics.setString("Count", String.valueOf(getCount()));
            }
        }
        return v;
    }



}
