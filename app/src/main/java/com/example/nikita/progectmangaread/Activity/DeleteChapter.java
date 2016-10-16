package com.example.nikita.progectmangaread.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nikita.progectmangaread.DataBasePMR.ClassDataBaseDownloadMang;
import com.example.nikita.progectmangaread.R;
import com.example.nikita.progectmangaread.cacheImage.CacheFile;
import com.example.nikita.progectmangaread.classPMR.ClassForList;
import com.example.nikita.progectmangaread.classPMR.ClassTransportForList;
import com.example.nikita.progectmangaread.service.ServiceDownChapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DeleteChapter extends DownloadChapter {
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        name = getIntent().getStringExtra("name");

        getSupportActionBar().setTitle("Delete chapter"); // set the top title


        Button downloadButton = (Button) findViewById(R.id.download_btn);
        downloadButton.setText("Удалить");
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameDir = "";
                String notDeleteDir = "";
                String notDeleteName = "";
                for (ClassForList classForList : list) {
                    if (classForList.getCheck())
                        nameDir += classForList.getURL_chapter()+",";
                    else {
                        notDeleteDir += classForList.getURL_chapter()+",";
                        notDeleteName += classForList.getName_chapter()+",";
                    }
                }
                if (!nameDir.isEmpty()) {
                    String namesDir[] = nameDir.split(",");
                    ArrayList<Integer> ints = new ArrayList();
                    for (String s : namesDir) {
                        CacheFile file = new CacheFile(getCacheDir(), s);
                        file.deleteDirectory();
                        for (ClassForList classForList : list)
                            if (classForList.getURL_chapter().equals(s))
                                ints.add(list.indexOf(classForList));
                    }
                    for (int i = 0; i < ints.size();i++){
                        list.remove(ints.get(i)-i);
                    }
                }

                ClassDataBaseDownloadMang classDataBaseDownloadMang = new ClassDataBaseDownloadMang(DeleteChapter.this);
                classDataBaseDownloadMang.setData(name,notDeleteDir, ClassDataBaseDownloadMang.NAME_DIR);
                classDataBaseDownloadMang.setData(name, notDeleteName, ClassDataBaseDownloadMang.NAME_CHAPTER);
             //   classDataBaseDownloadMang.closeDataBase();

                myAdap.notifyDataSetChanged();
                if (list.isEmpty())
                    DeleteChapter.this.finish();

            }
        });
    }
    public void onEvent(ClassTransportForList event){
        if (!event.getName().isEmpty()){
            list.clear();
            list.addAll(event.getClassForList());
            myAdap.notifyDataSetChanged();
        }
    }
}
