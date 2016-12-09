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

import com.zlobrynya.project.readermang.Activity.TopManga;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.classPMR.ClassRecentlyRead;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Nikita on 28.04.2016.
 */
public class AdapterRecentlyRead extends ArrayAdapter<ClassRecentlyRead> implements StickyListHeadersAdapter {
    private DisplayImageOptions options;
    private ArrayList<ClassRecentlyRead> item;

    public AdapterRecentlyRead(Context context, int resource, ArrayList<ClassRecentlyRead> item) {
        super(context, resource, item);
        this.item = item;
        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                .threadPoolSize(3)
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new LimitedAgeDiskCache(context.getApplicationContext().getCacheDir(), null, new HashCodeFileNameGenerator(), 60 * 60 * 30))
                .imageDownloader(new BaseImageDownloader(context)) // connectTimeout (5 s), readTimeout (30 s)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();

        imageLoader.init(config);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.f) // resource or drawable
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public long getHeaderId(int position) {
        return Long.parseLong(item.get(position).getDate().replace("-",""));
    }

    private class Holder {
        TextView nameChapter;
        TextView nameMang;
        ImageView img;
    }

    private class HeaderViewHolder {
        TextView text;
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
            v = vi.inflate(R.layout.recently_read, null);
            holder.img = (ImageView) v.findViewById(R.id.imageNameMangRR);
            holder.img.getLayoutParams().width = TopManga.WIDTH_WIND / 4;
            holder.img.getLayoutParams().height = TopManga.HEIGHT_WIND / 5;
            holder.nameChapter = (TextView) v.findViewById(R.id.textViewNameChapterRR);
            holder.nameMang = (TextView) v.findViewById(R.id.textViewNameMangRR);
            v.setTag(holder);
        }
        //получаем класс из позиции
        ClassRecentlyRead m1 = getItem(position);
        //если он есть то получаеи и устанавливаем изображение
        if (m1 != null){
            ImageLoader.getInstance().displayImage(m1.getURL_img(), holder.img,options);
            holder.nameMang.setText(m1.getNameMang());
            holder.nameChapter.setText(m1.getNameChapter());
        }
        return v;
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
        String headerText = item.get(position).getDate();
        holder.text.setText(headerText);
        return convertView;
    }
}
