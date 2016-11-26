package com.zlobrynya.project.ereadermanga;

import com.zlobrynya.project.ereadermanga.Activity.PagesDownload;
import com.zlobrynya.project.ereadermanga.cacheImage.CacheFile;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Nikita on 07.08.2016.
 * Менеджер потоков, для PageDownload, регулируют какую страницу качать
 */
public class ThreadManager {
    private ArrayList<Boolean> saveImg;
    private ArrayList<Integer> priorityImg;
    private ArrayList<String> urlPage;
    private CacheFile cacheFile;
    private boolean start;

    private AsyncTaskLisen as = new AsyncTaskLisen() {
        @Override
        public void onEnd() {

        }

        @Override
        public void onEnd(int number) {
            if (number > -1){
                saveImg.set(number,true);
            }

            for (int i = 0; i < priorityImg.size(); i++) {
                int numberPri = priorityImg.indexOf(i);
                if (!saveImg.get(numberPri)) {
                    cacheFile.loadAndCache(urlPage.get(numberPri), String.valueOf(numberPri));
                    break;
                }
            }
        }
    };


    public void setFalseSaveImg(int number){
        saveImg.set(number,false);
    }

    public ThreadManager(ArrayList<String> urlPage){
        this.urlPage = urlPage;
        saveImg = new ArrayList<>();
        priorityImg = new ArrayList<>();
        cacheFile = new CacheFile(new File(PagesDownload.pathDir), PagesDownload.nameDirectory,as);
        for(int i = 0; i < urlPage.size();i++){
            saveImg.add(false);
            priorityImg.add(i);
        }
        start = false;
    }

    public void stop(){
        cacheFile.forceStop();
    }

    //Приоритет скачивания страниц
    public void setPriorityImg(int number){
        if (number == 0){
            for (int i = 0; i < priorityImg.size();i++){
                priorityImg.set(i,i);
            }
        }else if (number == urlPage.size()-1) {
            priorityImg.set(priorityImg.size()-1,0);
            for (int i = number-1,j = 1; i >= 0;i--,j++)
                priorityImg.set(i,j);
        }else {
            priorityImg.set(number,0);
            priorityImg.set(number+1,1);
            priorityImg.set(number-1,2);
            for (int i = number-2,j = 1; i >= 0;i--,j++)
                priorityImg.set(i,priorityImg.size()-j);
            for (int i = number+2,j = 3; i < priorityImg.size();i++,j++)
                priorityImg.set(i,j);
        }
        if(!start){
            as.onEnd(-1);
        }
        start = true;
    }

    public Boolean isImageSave(int number){
        return saveImg.get(number);
    }

}
