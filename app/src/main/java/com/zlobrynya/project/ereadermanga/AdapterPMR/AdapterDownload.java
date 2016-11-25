package com.zlobrynya.project.ereadermanga.AdapterPMR;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zlobrynya.project.ereadermanga.Activity.DeleteChapterInList;
import com.zlobrynya.project.ereadermanga.Activity.TopManga;
import com.zlobrynya.project.ereadermanga.DataBasePMR.ClassDataBaseDownloadMang;
import com.zlobrynya.project.ereadermanga.R;
import com.zlobrynya.project.ereadermanga.classPMR.ClassRecentlyRead;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Nikita on 12.11.2016.
 */

public class AdapterDownload extends AdapterBookmark {
    private String path;

    public AdapterDownload(Context context, int resource, ArrayList<ClassRecentlyRead> item,String path) {
        super(context, resource, item);
        this.path = path;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
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
                    /*ClassDataBaseDownloadMang classData = new ClassDataBaseDownloadMang(context);
                    //Есть такая стр в бд naruto/vol72_699,naruto/vol72_698,
                    String nameDir = classData.getDataFromDataBase(item.get(poss).getNameMang(),ClassDataBaseDownloadMang.NAME_DIR)
                            .split(",")[0]  //достаем из нее naruto/vol72_699
                            .split("/")[0]; //потом достаем из naruto/vol72_699 вот это naruto
                    deleteImn(nameDir);
                    classData.deletRow(item.get(poss).getNameMang());
                    // classDataBaseViewedHead.closeDataBase();
                    Toast.makeText(context, "Delete: " + item.get(poss).getNameMang(),
                            Toast.LENGTH_SHORT).show();
                    item.remove(poss);
                    notifyDataSetChanged();*/
                    Intent newInten = new Intent(context,DeleteChapterInList.class);
                    newInten.putExtra("name",item.get(position).getNameMang());
                    context.startActivity(newInten);
                }
                //  Toast.makeText(TravelBite.this, "test", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void deleteImn(String nameDir){
        File[] dirs= new File(path+"/"+nameDir).listFiles();
        for(File dir: dirs){
            File chapters[] = dir.listFiles();
            if (chapters != null){
                for(File chapter: chapters){
                    chapter.delete();
                }
            }
            dir.delete();
        }
    }
}
