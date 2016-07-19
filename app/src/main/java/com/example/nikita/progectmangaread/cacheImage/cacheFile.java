package com.example.nikita.progectmangaread.cacheImage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.nikita.progectmangaread.AsyncTaskLisen;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nikita on 13.03.2016.
 * Class work with cache
 * Класс работы с кэшем
 */
public class CacheFile {
    private File dirFile;
    private AsyncTaskLisen as;
    private ProgressBar progressBar;

    public CacheFile(File dirFile, String nameDir, AsyncTaskLisen as, ProgressBar progressBar){
        this.dirFile = new File(dirFile,nameDir);
        if (!this.dirFile.exists()){
            this.dirFile.mkdir();
        }
        this.as = as;
        this.progressBar = progressBar;
    }

    public CacheFile(){
    }

    public CacheFile(File dirFile, String nameDir){
        this.dirFile = new File(dirFile,nameDir);
        if (!this.dirFile.exists()){
            this.dirFile.mkdir();
        }
        this.as = null;
        progressBar = null;
    }

    public void parameterSetting(File dirFile, String nameDir, AsyncTaskLisen as, ProgressBar progressBar){
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
        this.progressBar = progressBar;
    }

    //download image and cache it
    public void loadAndCache(String url, int number){
        class downlandImage extends AsyncTask<String,Integer,Void> {
            private int lenghtOfFile;
            @Override
            protected Void doInBackground(String... params) {
                try {
                    Log.i("Threads","CacheFile"+params[1]);
                    //продумать поименование файлов
                    File f = new File(dirFile, params[1]);
                    //Проверка на существование изображения
                    if (!f.exists()){
                        URL imageUrl = new URL(params[0]);
                        Log.i("File", params[0]);
                        HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                        conn.setConnectTimeout(30000);
                        conn.setReadTimeout(30000);
                        conn.connect();
                        lenghtOfFile = conn.getContentLength();
                        //InputStream is=conn.getInputStream();
                        InputStream is = new BufferedInputStream(imageUrl.openStream(), 8192);

                        if (params[0].contains("gif")){
                            FileOutputStream  out = new FileOutputStream(f);
                            BitmapFactory.decodeStream(is).compress(Bitmap.CompressFormat.PNG, 100, out);
                        }else {
                            OutputStream os = new FileOutputStream(f);
                            CopyStream(is, os);
                            os.close();
                        }
                        conn.disconnect();
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            //выводим в прогресс бар, сколько скачалось
            @Override
            protected void onProgressUpdate(Integer... values) {
                if (progressBar != null)
                    progressBar.setProgress(values[0]);
                //Log.i("ProgressBar", String.valueOf(values[0]));
                super.onProgressUpdate(values);
            }

            //Copy inputStream in OutputStream
            private void CopyStream(InputStream is, OutputStream os)
            {
                //буфер
                final int buffer_size=1024*12;
                try
                {
                    int total = 0;
                    byte[] bytes=new byte[buffer_size];
                    for(;;)
                    {
                        int count=is.read(bytes, 0, buffer_size);
                        if(count==-1)
                            break;
                        total += count;
                        publishProgress((int)((total*100)/lenghtOfFile));
                        os.write(bytes, 0, count);
                    }
                }
                catch(Exception ex){
                    Log.i("File","Copy Error");
                }
            }

            @Override
            protected void onPostExecute(Void result){
                if (as != null)
                    as.onEnd();
            }
        }

        downlandImage Task = new downlandImage();
        Task.execute(url, String.valueOf(number));
    }

    public String getFile(String nameCache) throws FileNotFoundException {
        File f = new File(dirFile, nameCache);
        Log.i("File",f.getPath());
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

    public void clearDownloadChapter(){
        dirFile.deleteOnExit();
    }
}
