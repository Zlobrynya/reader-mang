package com.zlobrynya.project.readermang.cacheImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.zlobrynya.project.readermang.AsyncTaskLisen;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Nikita on 13.03.2016.
 * Class work with cache
 * Класс работы с кэшем
 */
public class CacheFile {
    private File dirFile;
    private AsyncTaskLisen as;
    private DownlandImage downlandImage;
    private final String strLog = "CacheFile";
    private int total;
    private int numberImg;
    private boolean download;
    private int numberPage;
    private final boolean DEBUG = false;

    public CacheFile(File dirFile, String nameDir, AsyncTaskLisen as){
        this.dirFile = new File(dirFile,nameDir);
        if (!this.dirFile.exists()){
            this.dirFile.mkdir();
        }
        this.as = as;
        download = false;
    }

    public CacheFile(){
        dirFile = null;
        this.as = null;
        download = false;
    }

    public CacheFile(File dirFile, String nameDir){
        this.dirFile = new File(dirFile,nameDir);
        if (!this.dirFile.exists()){
            this.dirFile.mkdir();
        }
        this.as = null;
        download = false;
    }

    public void parameterSettingDownloadChapter(File dirFile, String nameDir, AsyncTaskLisen as){
        String[] namesDir = nameDir.split("/");
        File firsFile = new File(dirFile,namesDir[0]);
        if (!firsFile.exists()){
            firsFile.mkdir();
        }
        this.dirFile = new File(dirFile,nameDir);
        if (!this.dirFile.exists()){
            this.dirFile.mkdir();
        }
        this.as = as;
        download = true;
    }

    //download image and cache it
    public void loadAndCache(String url, String nameFile){
        downlandImage = new DownlandImage();
     //   Log.i("CacheFile: ","AsyncTask "+nameFile+ " start");
        downlandImage.execute(url, nameFile);
    }

    public boolean checkFileAndDownload(String url, String nameFile){
        File f = new File(dirFile, nameFile);
        if (f.exists()){
            if (as != null)
                as.onEnd();
            return true;
        }else {
            loadAndCache(url, nameFile);
            return false;
        }
    }

    public void deleteFile(String nameFile){
        File f = new File(dirFile, nameFile);
        if (f.exists()){
            f.delete();
        }
    }


    public boolean checkFile(String nameFile){
        File f = new File(dirFile, nameFile);
        return f.exists();
    }

    public String getFile(String nameCache) throws FileNotFoundException,EOFException {
        File f = new File(dirFile, nameCache);
      //  Log.i("File",f.getPath());
        //InputStream in = new FileInputStream(f);
        return f.getPath();
    }

    public void clearCache(){
        File[] files=dirFile.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

    public void deleteDirectory(){
        File[] files=dirFile.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();

        dirFile.delete();
    }

    public int getNumberOfFile(){
        return dirFile.listFiles().length;
    }

    //return false - если не остановили поток, return true - если поток остановлен
    public boolean stopAsyncTask() {
        if (downlandImage != null && total < 20) {
           // Log.i("CacheFile: ", "AsyncTask " + numberImg + " stop");
            downlandImage.cancel(true);
            return true;
        }
        return downlandImage == null || downlandImage.getStatus() != AsyncTask.Status.RUNNING;
    }

    public void forceStop(){
        if (downlandImage != null)
            downlandImage.cancel(true);
    }

    private class DownlandImage extends AsyncTask<String,Integer,Void> {
        private int lenghtOfFile;
        private boolean compress;
        private boolean error = false;

        @Override
        protected Void doInBackground(String... params) {
            try {
                total = 0;
             //   compress = false;
                if (DEBUG)
                    Log.i("Threads","CacheFile"+params[1]);
                //продумать поименование файлов
                File f = new File(dirFile, params[1]);
                try {
                    numberImg = Integer.parseInt(params[1]);
                } catch (NumberFormatException e) {
                    numberImg = -1;
                }
                //Проверка на существование изображения
                if (!f.exists()){
                    URL imageUrl = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                    conn.connect();
                    lenghtOfFile = conn.getContentLength();
                    //InputStream is=conn.getInputStream();
                    InputStream is = new BufferedInputStream(imageUrl.openStream(), 8192);

                    if (!params[0].contains("png")){
                        compress = true;
                       /* ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                        BitmapFactory.decodeStream(is).compress(Bitmap.CompressFormat.PNG, 100, byteOutputStream);
                        OutputStream os = new FileOutputStream(f);
                        byte[] mbitmapdata = byteOutputStream.toByteArray();
                        lenghtOfFile = mbitmapdata.length;
                        CopyStream(new ByteArrayInputStream(mbitmapdata), os); */
                    }
                    OutputStream os = new FileOutputStream(f);
                    CopyStream(is, os);
                    conn.disconnect();
                    //Изменил для теста
                    if (compress)
                        compressPng(f,os);
                    os.close();
                    if (DEBUG)
                        Log.i("File", params[0]+" Complite");
                }

            }catch (IOException e) {
                error = true;
                e.printStackTrace();
            }
            return null;
        }

        //Copy inputStream in OutputStream
        private void CopyStream(InputStream is, OutputStream os)
        {
            //буфер
            final int buffer_size=1024*12;
            try
            {
                int total = 0;
                byte[] bytes = new byte[buffer_size];
                for(;;)
                {
                    int count = is.read(bytes, 0, buffer_size);
                    if(count == -1)
                        break;
                    total += count;
                   // publishProgress((int)((total*100)/lenghtOfFile));
                    os.write(bytes, 0, count);
                }
            }
            catch(Exception ex){
                Log.i("File","Copy Error");
            }
        }

        private void compressPng(File file, OutputStream os){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                BitmapFactory.decodeStream(fileInputStream).compress(Bitmap.CompressFormat.PNG, 100, os);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException ignored){

            }
        }

        @Override
        protected void onPostExecute(Void result){
            if (isCancelled() || error){
                File f = new File(dirFile, String.valueOf(numberImg));
                if (f.exists()) {
                    f.delete();
                }
                if (as != null)
                    as.onEnd(-1);
            }else {
                if (as != null)
                    if (numberImg != -1 && !download){
                        EventBus.getDefault().post(numberImg+"/Start");
                        as.onEnd(numberImg);
                    }else
                        as.onEnd();
            }
        }
    }
}
