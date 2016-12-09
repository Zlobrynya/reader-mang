package com.zlobrynya.project.readermang.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zlobrynya.project.readermang.DataBasePMR.ClassDataBaseDownloadMang;
import com.zlobrynya.project.readermang.R;
import com.zlobrynya.project.readermang.cacheImage.CacheFile;
import com.zlobrynya.project.readermang.classPMR.ClassForList;
import com.zlobrynya.project.readermang.classPMR.ClassTransportForList;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

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
                    if (classForList.isCheck())
                        nameDir += classForList.getURLChapter()+",";
                    else {
                        notDeleteDir += classForList.getURLChapter()+",";
                        notDeleteName += classForList.getNameChapter()+",";
                    }
                }
                if (!nameDir.isEmpty()) {
                    String namesDir[] = nameDir.split(",");
                    ArrayList<Integer> ints = new ArrayList();
                    for (String s : namesDir) {
                        CacheFile file = new CacheFile(getCacheDir(), s);
                        file.deleteDirectory();
                        for (ClassForList classForList : list)
                            if (classForList.getURLChapter().equals(s))
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ClassTransportForList event){
        if (!event.getName().isEmpty()){
            list.clear();
            list.addAll(event.getClassForList());
            myAdap.notifyDataSetChanged();
        }
    }
}
